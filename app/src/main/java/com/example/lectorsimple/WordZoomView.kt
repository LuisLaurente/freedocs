package com.example.lectorsimple

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

/** Recycles small text blocks instead of laying out an entire Word document at once. */
class WordZoomView(context: Context, text: String, state: Bundle?) : ListView(context) {
    private val blocks = WordText.splitBlocks(text)
    private var zoom = state?.getFloat("zoom", 1f)?.coerceIn(0.75f, 3f) ?: 1f
    private var anchor = 0
    private var anchorOffset = 0
    private var startingZoom = 1f
    private var restorePosition = state
    private val textAdapter = object : BaseAdapter() {
        override fun getCount() = blocks.size
        override fun getItem(position: Int) = blocks[position]
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val row = convertView as? TextView ?: TextView(context).apply {
                setTextColor(Color.rgb(35, 39, 45))
                setLineSpacing(0f, 1.2f)
                val padding = (16 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding / 2, padding, padding / 2)
            }
            row.text = blocks[position]
            row.textSize = 18f * zoom
            return row
        }
    }
    private val detector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            anchor = firstVisiblePosition
            anchorOffset = getChildAt(0)?.top ?: 0
            startingZoom = zoom
            return true
        }
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val next = (zoom * detector.scaleFactor).coerceIn(0.75f, 3f)
            if (next != zoom) {
                zoom = next
                textAdapter.notifyDataSetChanged()
                setSelectionFromTop(anchor, (anchorOffset * zoom / startingZoom).toInt())
            }
            return true
        }
    })

    init {
        setBackgroundColor(Color.WHITE)
        divider = null
        dividerHeight = 0
        adapter = textAdapter
        state?.let { setSelection(it.getInt("block", 0).coerceIn(blocks.indices)) }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        if (event.pointerCount > 1 || detector.isInProgress) {
            val cancel = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
            super.dispatchTouchEvent(cancel)
            cancel.recycle()
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val state = restorePosition ?: return
        val first = getChildAt(0) ?: return
        restorePosition = null
        setSelectionFromTop(state.getInt("block", 0).coerceIn(blocks.indices), (state.getFloat("offset", 0f) * first.height).toInt())
    }

    fun savePosition(): Bundle = restorePosition ?: Bundle().apply {
        putFloat("zoom", zoom)
        putInt("block", firstVisiblePosition)
        val first = getChildAt(0)
        putFloat("offset", (first?.top ?: 0).toFloat() / (first?.height ?: 1).coerceAtLeast(1))
    }

}
