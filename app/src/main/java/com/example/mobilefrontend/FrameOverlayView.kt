package com.example.mobilefrontend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class FrameOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.WHITE      // The color of the frame
        style = Paint.Style.STROKE
        strokeWidth = 5f         // Thickness of the frame
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Compute a centered rectangle
        val frameWidth = width * 0.6f
        val frameHeight = frameWidth * (8.8f / 6.3f) // approx frameWidth * 1.4 for a vertical card
        val left = (width - frameWidth) / 2
        val top = (height - frameHeight) / 2
        val right = left + frameWidth
        val bottom = top + frameHeight

        // Draw the rectangle
        canvas.drawRect(left, top, right, bottom, paint)

        // Optional: Draw corner markers or guides
        canvas.drawLine(left, top, left + 50f, top, paint) // Top-left corner
        canvas.drawLine(left, top, left, top + 50f, paint)
        canvas.drawLine(right, top, right - 50f, top, paint) // Top-right corner
        canvas.drawLine(right, top, right, top + 50f, paint)
        canvas.drawLine(left, bottom, left + 50f, bottom, paint) // Bottom-left corner
        canvas.drawLine(left, bottom, left, bottom - 50f, paint)
        canvas.drawLine(right, bottom, right - 50f, bottom, paint) // Bottom-right corner
        canvas.drawLine(right, bottom, right, bottom - 50f, paint)
    }
}