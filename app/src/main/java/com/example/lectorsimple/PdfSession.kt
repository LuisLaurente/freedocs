package com.example.lectorsimple

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.Closeable
import java.io.File
import kotlin.math.ceil

internal const val PDF_TILE_SIZE = 512
data class PdfTileKey(val level: Int, val column: Int, val row: Int, val viewWidth: Int)
data class PdfViewport(val page: Int, val scale: Float, val tiles: List<PdfTileKey>, val preview: Boolean)
data class PdfFrame(val page: Int, val key: PdfTileKey?, val bitmap: Bitmap, val bounds: android.graphics.RectF)

/** All access is serialized by MainActivity's worker, including close. */
class PdfSession(private val file: File) : Closeable {
    private val renderer: PdfRenderer
    val pageCount: Int get() = renderer.pageCount

    init {
        val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        try {
            renderer = PdfRenderer(descriptor)
            if (renderer.pageCount == 0) {
                renderer.close()
                throw ReadingException("El PDF no contiene páginas.")
            }
        } catch (failure: Throwable) {
            descriptor.close()
            file.delete()
            throw failure
        }
    }

    fun pageSize(index: Int): Pair<Int, Int> = renderer.openPage(index).use { it.width to it.height }

    fun render(viewport: PdfViewport, deliver: (PdfFrame) -> Unit) {
        DocumentReader.checkCancelled()
        renderer.openPage(viewport.page).use { page ->
            if (viewport.preview) {
                // Full-page fallback, capped at 262,144 pixels (1 MiB).
                val scale = minOf(1.0, kotlin.math.sqrt(262_144.0 / (page.width.toDouble() * page.height))).toFloat()
                val bounds = android.graphics.RectF(0f, 0f, page.width.toFloat(), page.height.toFloat())
                deliver(rasterize(page, null, bounds, scale))
            }
            for (key in viewport.tiles) {
                DocumentReader.checkCancelled()
                val left = key.column * PDF_TILE_SIZE / viewport.scale
                val top = key.row * PDF_TILE_SIZE / viewport.scale
                val right = minOf(page.width.toFloat(), (key.column + 1) * PDF_TILE_SIZE / viewport.scale)
                val bottom = minOf(page.height.toFloat(), (key.row + 1) * PDF_TILE_SIZE / viewport.scale)
                if (right > left && bottom > top) {
                    deliver(rasterize(page, key, android.graphics.RectF(left, top, right, bottom), viewport.scale))
                }
            }
        }
    }

    private fun rasterize(page: PdfRenderer.Page, key: PdfTileKey?, bounds: android.graphics.RectF, scale: Float): PdfFrame {
        DocumentReader.checkCancelled()
        val width = ceil(bounds.width() * scale).toInt().coerceAtLeast(1)
        val height = ceil(bounds.height() * scale).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            bitmap.eraseColor(Color.WHITE)
            val matrix = Matrix().apply {
                setValues(floatArrayOf(
                    scale, 0f, -bounds.left * scale,
                    0f, scale, -bounds.top * scale,
                    0f, 0f, 1f
                ))
            }
            page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return PdfFrame(page.index, key, bitmap, bounds)
        } catch (failure: Throwable) {
            bitmap.recycle()
            throw failure
        }
    }

    override fun close() {
        try { renderer.close() } finally { file.delete() }
    }
}
