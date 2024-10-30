package uz.gita.quiz_demo.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class HexagonButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val textPaint = Paint()
    private val hexagonPath = Path()
    private var text = "H" // Default text
    private val textSize = 40f

    init {
        // Paint setup for hexagon with gradient shader
        paint.style = Paint.Style.FILL

        // Paint setup for text
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createHexagonPath(w, h)

        // Set up the gradient (top-to-bottom gradient)
        val gradient = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            Color.parseColor("#FFDF00"), // Start color (teal)
            Color.parseColor("#FF8C00"), // End color (blue)
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
    }

    private fun createHexagonPath(width: Int, height: Int) {
        val radius = min(width, height) / 2f
        val centerX = width / 2f
        val centerY = height / 2f

        hexagonPath.reset()

        // Calculate the 6 points of the hexagon
        for (i in 0 until 6) {
            val angle = Math.toRadians((i * 60 - 90).toDouble()).toFloat()
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)

            if (i == 0) {
                hexagonPath.moveTo(x, y)
            } else {
                hexagonPath.lineTo(x, y)
            }
        }
        hexagonPath.close()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw hexagon with gradient background
        canvas.drawPath(hexagonPath, paint)

        // Draw centered text
        val xPos = width / 2f
        val yPos = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)
    }
    // Function to set text programmatically
    fun setText(newText: String) {
        text = newText
        invalidate() // Redraw to update the text
    }
}