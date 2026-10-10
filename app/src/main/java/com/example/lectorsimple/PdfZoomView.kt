package com.example.lectorsimple

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import kotlin.math.max
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/** Page preview plus reusable tiles. All bitmap/cache ownership stays on the UI thread. */
class PdfZoomView(context: Context, private val requestFrame: (PdfViewport) -> Unit) : View(context) {
    private var page = -1
    private var pageWidth = 0
    private var pageHeight = 0
    private var zoom = 1f
    private var x = 0f
    private var y = 0f
    private var preview: PdfFrame? = null
    private val tiles = LinkedHashMap<PdfTileKey, PdfFrame>(24, 0.75f, true)
    private var tileBytes = 0L
    private val pageRect = RectF()
    private var wanted = emptySet<PdfTileKey>()
    private var restore: Bundle? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val scroller = OverScroller(context)
    private var renderPosted = false
    private val render = Runnable {
        renderPosted = false
        viewport()?.let(requestFrame)
    }
    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scroller.forceFinished(true)
            return true
        }
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            changeZoom(zoom * detector.scaleFactor, detector.focusX, detector.focusY)
            return true
        }
        override fun onScaleEnd(detector: ScaleGestureDetector) { scheduleRender() }
    })
    private val gestures = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            scroller.forceFinished(true)
            return true
        }
        override fun onScroll(first: MotionEvent?, event: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!scaleDetector.isInProgress && event.pointerCount == 1) {
                x -= distanceX
                y -= distanceY
                constrain()
                invalidate()
                scheduleRender()
            }
            return true
        }
        override fun onDoubleTap(event: MotionEvent): Boolean {
            changeZoom(if (zoom > 1.05f) 1f else 2.5f, event.x, event.y)
            return true
        }
        override fun onSingleTapConfirmed(event: MotionEvent): Boolean { performClick(); return true }
        override fun onFling(first: MotionEvent?, event: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (scaleDetector.isInProgress || pageWidth == 0) return false
            val scale = width.toFloat() / pageWidth * zoom
            val minX = if (pageWidth * scale > width) (width - pageWidth * scale).toInt() else x.toInt()
            val maxX = if (pageWidth * scale > width) 0 else x.toInt()
            val minY = if (pageHeight * scale > height) (height - pageHeight * scale).toInt() else y.toInt()
            val maxY = if (pageHeight * scale > height) 0 else y.toInt()
            scroller.fling(x.toInt(), y.toInt(), velocityX.toInt(), velocityY.toInt(), minX, maxX, minY, maxY)
            postInvalidateOnAnimation()
            return true
        }
    })

    init {
        setBackgroundColor(Color.BLACK)
        contentDescription = "Página PDF. Pellizca para ampliar y desliza para desplazarte."
        isFocusable = true
    }

    fun showPage(index: Int, dimensions: Pair<Int, Int>, state: Bundle? = null) {
        releaseFrame()
        scroller.forceFinished(true)
        page = index
        pageWidth = dimensions.first
        pageHeight = dimensions.second
        zoom = 1f
        x = 0f
        y = 0f
        restore = state
        applyRestore()
        constrain()
        invalidate()
        scheduleRender()
    }

    private fun applyRestore() {
        if (width == 0) return
        restore?.let {
            zoom = it.getFloat("zoom", 1f).coerceIn(1f, 6f)
            x = it.getFloat("x", 0f) * width
            y = it.getFloat("y", 0f) * width
        }
        restore = null
    }

    fun savePosition(): Bundle = restore ?: Bundle().apply {
        putFloat("zoom", zoom)
        putFloat("x", x / max(1, width))
        putFloat("y", y / max(1, width))
    }

    private fun viewport(): PdfViewport? {
        if (width <= 0 || height <= 0 || pageWidth <= 0) return null
        if (scaleDetector.isInProgress && preview != null) return null
        val displayScale = width.toFloat() / pageWidth * zoom
        // Discrete resolution levels let tiles survive pans and small zoom changes.
        // Cap visible detail at 2 million pixels on exceptionally large screens.
        val factor = minOf(1.0, sqrt(2_000_000.0 / (width.toDouble() * height))).toFloat()
        val level = floor(ln((zoom * factor).toDouble()) / ln(2.0) * 2).toInt()
        val scale = width.toFloat() / pageWidth * 2.0.pow(level / 2.0).toFloat()
        val columns = ceil(pageWidth * scale / PDF_TILE_SIZE).toInt().coerceAtLeast(1)
        val rows = ceil(pageHeight * scale / PDF_TILE_SIZE).toInt().coerceAtLeast(1)
        val left = floor(-x / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, columns - 1)
        val right = floor((width - x) / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, columns - 1)
        val top = floor(-y / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, rows - 1)
        val bottom = floor((height - y) / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, rows - 1)
        val keys = ArrayList<PdfTileKey>()
        for (row in top..bottom) for (column in left..right) keys.add(PdfTileKey(level, column, row, width))
        wanted = keys.toSet()
        val missing = keys.filter { tiles[it] == null }.sortedBy {
            val dx = it.column - (left + right) / 2f
            val dy = it.row - (top + bottom) / 2f
            dx * dx + dy * dy
        }
        if (missing.isEmpty() && preview != null) return null
        return PdfViewport(page, scale, missing, preview == null)
    }

    fun displayFrame(value: PdfFrame) {
        if (value.page != page || !isAttachedToWindow) { value.bitmap.recycle(); return }
        val key = value.key
        if (key != null && key.viewWidth != width) { value.bitmap.recycle(); return }
        if (key == null) {
            if (preview == null) preview = value else value.bitmap.recycle()
        } else {
            tiles.put(key, value)?.let { tileBytes -= it.bitmap.allocationByteCount; it.bitmap.recycle() }
            tileBytes += value.bitmap.allocationByteCount
            while (tileBytes > 16L * 1024 * 1024 && tiles.size > 1) {
                // Evict old/offscreen detail before currently visible tiles.
                val evictedKey = tiles.keys.firstOrNull { it !in wanted } ?: tiles.keys.first()
                val evicted = tiles.remove(evictedKey)!!
                tileBytes -= evicted.bitmap.allocationByteCount
                evicted.bitmap.recycle()
            }
        }
        invalidate()
    }

    private fun changeZoom(value: Float, focusX: Float, focusY: Float) {
        val next = value.coerceIn(1f, 6f)
        val ratio = next / zoom
        x = focusX - (focusX - x) * ratio
        y = focusY - (focusY - y) * ratio
        zoom = next
        constrain()
        invalidate()
        scheduleRender()
    }

    private fun constrain() {
        if (pageWidth <= 0 || width <= 0) return
        val scale = width.toFloat() / pageWidth * zoom
        val scaledWidth = pageWidth * scale
        val scaledHeight = pageHeight * scale
        x = if (scaledWidth <= width) (width - scaledWidth) / 2 else x.coerceIn(width - scaledWidth, 0f)
        y = if (scaledHeight <= height) (height - scaledHeight) / 2 else y.coerceIn(height - scaledHeight, 0f)
    }

    private fun scheduleRender() {
        if (!renderPosted) { renderPosted = true; postDelayed(render, 32) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Tile levels depend on the view width; the full-page preview is reusable.
        tiles.values.forEach { it.bitmap.recycle() }
        tiles.clear()
        tileBytes = 0
        if (oldw > 0) { x *= w.toFloat() / oldw; y *= w.toFloat() / oldw }
        applyRestore()
        constrain()
        scheduleRender()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pageWidth <= 0 || width <= 0) return
        val scale = width.toFloat() / pageWidth * zoom
        canvas.save()
        canvas.translate(x, y)
        canvas.scale(scale, scale)
        pageRect.set(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
        canvas.clipRect(pageRect)
        preview?.let { canvas.drawBitmap(it.bitmap, null, pageRect, paint) }
        // Draw older levels first so current detail covers them without holes.
        val level = wanted.firstOrNull()?.level
        for ((key, image) in tiles) if (key.level != level && visible(image.bounds, scale)) {
            canvas.drawBitmap(image.bitmap, null, image.bounds, paint)
        }
        for ((key, image) in tiles) if (key.level == level && visible(image.bounds, scale)) {
            canvas.drawBitmap(image.bitmap, null, image.bounds, paint)
        }
        canvas.restore()
    }

    private fun visible(bounds: RectF, scale: Float) =
        bounds.right * scale + x > 0 && bounds.left * scale + x < width &&
            bounds.bottom * scale + y > 0 && bounds.top * scale + y < height

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        scaleDetector.onTouchEvent(event)
        gestures.onTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) scheduleRender()
        return true
    }

    override fun performClick(): Boolean { super.performClick(); return true }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            x = scroller.currX.toFloat()
            y = scroller.currY.toFloat()
            constrain()
            scheduleRender()
            postInvalidateOnAnimation()
        }
    }

    private fun releaseFrame() {
        preview?.bitmap?.recycle()
        preview = null
        tiles.values.forEach { it.bitmap.recycle() }
        tiles.clear()
        tileBytes = 0
        wanted = emptySet()
    }
    override fun onDetachedFromWindow() {
        removeCallbacks(render)
        renderPosted = false
        scroller.forceFinished(true)
        releaseFrame()
        super.onDetachedFromWindow()
    }
}
