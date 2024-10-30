package uz.gita.quiz_demo.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import uz.gita.quiz_demo.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class HexagonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val path = Path()
    private var cornerRadius = 12f

    var symbol: String = "A"
        set(value) {
            field = value.uppercase()
            invalidate()
        }

    private var fontFamily: Typeface? = ResourcesCompat.getFont(context, R.font.gilroy_bold)
        set(value) {
            field = value
            textPaint.typeface = value
            invalidate()
        }

    var isGradientEnabled: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    private var shadowColor: Int = Color.parseColor("#80B8E6")
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    private var shadowRadius: Float = 10f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    private var shadowX: Float = 0f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    private var shadowY: Float = 0f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    private val hexagonPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val shadowPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor)
    }

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        color = if (isGradientEnabled) Color.WHITE else Color.parseColor("#C1E1F9")
        textAlign = Paint.Align.CENTER
        typeface = fontFamily
    }

    private var gradientStartColor: Int = Color.parseColor("#FFDF00")
    private var gradientEndColor: Int = Color.parseColor("#FF8C00")

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.HexagonView)
            symbol = typedArray.getString(R.styleable.HexagonView_characterSymbol) ?: symbol
            isGradientEnabled = typedArray.getBoolean(R.styleable.HexagonView_isGradientEnabled, true)
            shadowColor = typedArray.getColor(R.styleable.HexagonView_shadowColor, shadowColor)

            shadowRadius = typedArray.getDimension(R.styleable.HexagonView_shadowRadius, shadowRadius)
            shadowX = typedArray.getDimension(R.styleable.HexagonView_shadowX, shadowX)
            shadowY = typedArray.getDimension(R.styleable.HexagonView_shadowY, shadowY)

            gradientStartColor = typedArray.getColor(R.styleable.HexagonView_gradientStartColor, gradientStartColor)
            gradientEndColor = typedArray.getColor(R.styleable.HexagonView_gradientEndColor, gradientEndColor)

            val fontFamilyResourceId = typedArray.getResourceId(R.styleable.HexagonView_android_fontFamily, -1)
            if (fontFamilyResourceId != -1) {
                fontFamily = ResourcesCompat.getFont(context, fontFamilyResourceId)
            }

            typedArray.recycle()
        }

        updateShadow()
    }

    private fun updateShadow() {
        shadowPaint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createRoundedHexagonPath(w, h)

        textPaint.textSize = h * 0.6f * (2.0f / 3.0f)
    }

    private fun createRoundedHexagonPath(width: Int, height: Int) {
        val midX = width / 2f
        val midY = height / 2f
        val radius = min(midX, midY)

        path.reset()
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i).toDouble() - 30)
            val x = (midX + radius * cos(angle)).toFloat()
            val y = (midY + radius * sin(angle)).toFloat()

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        val cornerEffect = CornerPathEffect(cornerRadius)
        hexagonPaint.pathEffect = cornerEffect
        shadowPaint.pathEffect = cornerEffect
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(size, size)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        canvas.drawPath(path, shadowPaint)

        if (isGradientEnabled) {
            val shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(gradientStartColor, gradientEndColor), null, Shader.TileMode.CLAMP
            )
            hexagonPaint.shader = shader
            textPaint.color = Color.WHITE
        } else {
            hexagonPaint.shader = null
            hexagonPaint.color = Color.parseColor("#FFFFFF")
            textPaint.color = Color.parseColor("#C1E1F9")
        }

        canvas.drawPath(path, hexagonPaint)

        val midX = width / 2f
        val midY = height / 2f
        canvas.drawText(symbol.uppercase(), midX, midY - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
    }
}