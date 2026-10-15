package com.example.lectorsimple

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.InflaterInputStream
import kotlin.math.abs

enum class PdfTool { REPLACE_TEXT, ADD_TEXT, MOVE, INK, HIGHLIGHT }

data class PdfTextEdit(
    val id: Long,
    val page: Int,
    val bounds: RectF,
    var text: String,
    var size: Float = 12f,
    var color: Int = Color.BLACK,
    var background: Int = Color.WHITE,
    var coverOriginal: Boolean = false
)

data class PdfStrokeEdit(val page: Int, val points: List<PointF>, val color: Int, val width: Float, val highlight: Boolean)
data class PdfPageEdit(val page: Int, val texts: MutableList<PdfTextEdit> = mutableListOf(), val strokes: MutableList<PdfStrokeEdit> = mutableListOf())

class PdfDocumentEdits {
    private val pages = linkedMapOf<Int, PdfPageEdit>()
    private val history = ArrayDeque<() -> Unit>()
    private fun page(index: Int) = pages.getOrPut(index) { PdfPageEdit(index) }
    fun texts(index: Int): List<PdfTextEdit> = pages[index]?.texts.orEmpty()
    fun strokes(index: Int): List<PdfStrokeEdit> = pages[index]?.strokes.orEmpty()
    fun add(edit: PdfTextEdit) { page(edit.page).texts.add(edit); history.addLast { page(edit.page).texts.remove(edit) } }
    fun add(edit: PdfStrokeEdit) { page(edit.page).strokes.add(edit); history.addLast { page(edit.page).strokes.remove(edit) } }
    fun remove(edit: PdfTextEdit) { val p = page(edit.page); val i = p.texts.indexOf(edit); if (i >= 0) { p.texts.removeAt(i); history.addLast { p.texts.add(i.coerceAtMost(p.texts.size), edit) } } }
    fun move(edit: PdfTextEdit, bounds: RectF) { val old = RectF(edit.bounds); edit.bounds.set(bounds); history.addLast { edit.bounds.set(old) } }
    fun recordMove(edit: PdfTextEdit, oldBounds: RectF) { val old = RectF(oldBounds); history.addLast { edit.bounds.set(old) } }
    fun clearPage(index: Int) { val old = pages.remove(index) ?: return; history.addLast { pages[index] = old } }
    fun clearAll() { val old = LinkedHashMap(pages); pages.clear(); history.addLast { pages.putAll(old) } }
    fun reset() { pages.clear(); history.clear() }
    fun undo(): Boolean { if (history.isEmpty()) return false; history.removeLast().invoke(); return true }
    fun hasEdits() = pages.values.any { it.texts.isNotEmpty() || it.strokes.isNotEmpty() }
}

object PdfEditRenderer {
    fun drawPageEdits(canvas: Canvas, edits: PdfDocumentEdits, page: Int) {
        edits.strokes(page).forEach { stroke ->
            if (stroke.points.isEmpty()) return@forEach
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (stroke.highlight) (stroke.color and 0x00ffffff) or 0x55000000 else stroke.color
                style = Paint.Style.STROKE; strokeWidth = stroke.width; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
            }
            val path = Path().apply { moveTo(stroke.points.first().x, stroke.points.first().y); stroke.points.drop(1).forEach { lineTo(it.x, it.y) } }
            canvas.drawPath(path, paint)
        }
        edits.texts(page).forEach { edit ->
            if (edit.coverOriginal) canvas.drawRect(edit.bounds, Paint().apply { color = edit.background; style = Paint.Style.FILL })
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                color = edit.color; textSize = edit.size; isSubpixelText = true; isLinearText = true
            }
            val width = edit.bounds.width().toInt().coerceAtLeast(1)
            val layout = StaticLayout.Builder.obtain(edit.text, 0, edit.text.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false).setLineSpacing(0f, 1f).build()
            canvas.save(); canvas.translate(edit.bounds.left, edit.bounds.top); layout.draw(canvas); canvas.restore()
        }
    }
}

object VisualLineDetector {
    data class Result(val bounds: RectF, val background: Int)

    fun detect(bitmap: Bitmap, pageWidth: Int, pageHeight: Int, x: Float, y: Float): Result? {
        if (bitmap.width < 3 || bitmap.height < 3) return null
        val sx = bitmap.width.toFloat() / pageWidth; val sy = bitmap.height.toFloat() / pageHeight
        val px = (x * sx).toInt().coerceIn(1, bitmap.width - 2); val py = (y * sy).toInt().coerceIn(1, bitmap.height - 2)
        val paper = medianColor(intArrayOf(bitmap.getPixel(1, 1), bitmap.getPixel(bitmap.width - 2, 1), bitmap.getPixel(1, bitmap.height - 2), bitmap.getPixel(bitmap.width - 2, bitmap.height - 2)))
        fun ink(row: Int): Boolean {
            for (xx in maxOf(0, px - 3)..minOf(bitmap.width - 1, px + 3)) if (colorDistance(bitmap.getPixel(xx, row), paper) > 55) return true
            return false
        }
        if (!ink(py) && !ink(py - 1) && !ink(py + 1)) return null
        var top = py; var bottom = py
        while (top > 0 && ink(top - 1) && py - top < bitmap.height / 30) top--
        while (bottom < bitmap.height - 1 && ink(bottom + 1) && bottom - py < bitmap.height / 30) bottom++
        top = maxOf(0, top - 2); bottom = minOf(bitmap.height - 1, bottom + 2)
        var left = bitmap.width - 1; var right = 0
        for (row in top..bottom) for (xx in 0 until bitmap.width) if (colorDistance(bitmap.getPixel(xx, row), paper) > 55) { left = minOf(left, xx); right = maxOf(right, xx) }
        if (right <= left) return null
        return Result(RectF(left / sx, top / sy, (right + 1) / sx, (bottom + 1) / sy), paper)
    }

    private fun medianColor(colors: IntArray): Int {
        val r = colors.map { Color.red(it) }.sorted()[colors.size / 2]; val g = colors.map { Color.green(it) }.sorted()[colors.size / 2]; val b = colors.map { Color.blue(it) }.sorted()[colors.size / 2]
        return Color.rgb(r, g, b)
    }
    private fun colorDistance(a: Int, b: Int) = abs(Color.red(a) - Color.red(b)) + abs(Color.green(a) - Color.green(b)) + abs(Color.blue(a) - Color.blue(b))
}

/** Lightweight extraction of standard PDF text operators for line prefill. */
object PdfTextExtractor {
    fun extract(file: File): String {
        val bytes = file.readBytes()
        val result = StringBuilder()
        val source = bytes.toString(Charsets.ISO_8859_1)
        val streamPattern = Regex("(?s)(<<.*?>>)\\s*stream\\r?\\n(.*?)\\r?\\nendstream")
        for (match in streamPattern.findAll(source)) {
            val raw = match.groupValues[2].toByteArray(Charsets.ISO_8859_1)
            val decoded = try { if (match.groupValues[1].contains("FlateDecode")) InflaterInputStream(raw.inputStream()).use { it.readBytes() } else raw } catch (_: Exception) { continue }
            val content = decoded.toString(Charsets.ISO_8859_1)
            for (block in Regex("(?s)BT(.*?)ET").findAll(content)) {
                val operators = block.groupValues[1]
                val strings = Regex("\\((?:\\\\.|[^\\\\)])*\\)\\s*Tj|\\[(.*?)\\]\\s*TJ|\\((?:\\\\.|[^\\\\)])*\\)\\s*'|\\((?:\\\\.|[^\\\\)])*\\)\\s*\\\"")
                for (op in strings.findAll(operators)) {
                    val tokens = Regex("\\((?:\\\\.|[^\\\\)])*\\)|<([0-9A-Fa-f]+)>").findAll(op.value)
                    for (token in tokens) result.append(decodePdfString(token.value)).append(' ')
                }
                result.append('\n')
            }
        }
        return result.toString().trim()
    }

    private fun decodePdfString(token: String): String {
        if (token.startsWith("<")) {
            val bytes = token.trim('<', '>').chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
            return if (bytes.size >= 2 && bytes[0] == 0xfe.toByte() && bytes[1] == 0xff.toByte()) bytes.copyOfRange(2, bytes.size).toString(Charsets.UTF_16BE) else bytes.toString(Charsets.ISO_8859_1)
        }
        val body = token.substring(1, token.lastIndex)
        return Regex("\\\\([nrtbf()\\\\]|[0-7]{1,3}|\\r?\\n)").replace(body) { m ->
            when (val c = m.groupValues[1]) { "n" -> "\n"; "r" -> "\r"; "t" -> "\t"; "b" -> "\b"; "f" -> "\u000c"; "\n", "\r\n" -> ""; else -> if (c.firstOrNull()?.isDigit() == true) c.toInt(8).toChar().toString() else c }
        }
    }
}

object PdfSaver {
    fun saveEditedPdf(session: PdfSession, edits: PdfDocumentEdits, outputFile: File) {
        val document = PdfDocument()
        try {
            for (index in 0 until session.pageCount) {
                DocumentReader.checkCancelled()
                val (width, height) = session.pageSize(index)
                val page = document.startPage(PdfDocument.PageInfo.Builder(width, height, index + 1).create())
                val bitmap = session.renderPageBitmap(index, 2f)
                try {
                    page.canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), Paint(Paint.FILTER_BITMAP_FLAG))
                } finally { bitmap.recycle() }
                PdfEditRenderer.drawPageEdits(page.canvas, edits, index)
                document.finishPage(page)
            }
            FileOutputStream(outputFile).use { document.writeTo(it) }
        } catch (failure: Throwable) {
            outputFile.delete()
            throw failure
        } finally { document.close() }
    }
}
