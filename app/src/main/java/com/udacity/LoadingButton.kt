package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonTitle: String
    private var animateWidth = 0f
    private var animateCircle = 0f
    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)

    private var valueAnimator = ValueAnimator()
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonTitle = resources.getString(R.string.button_download)
                invalidate()
            }
            ButtonState.Loading -> {
                buttonTitle = resources.getString(R.string.button_loading)
                valueAnimator.duration = 2000
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.addUpdateListener { animation ->
                    animateWidth = animation.animatedValue as Float
                    animateCircle = (widthSize.toFloat() / 365) * animateWidth
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        isEnabled = false
                    }
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        animateWidth = 0f
                        isEnabled = true
                        buttonState = ButtonState.Completed
                    }
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                animateWidth = 0f
                animateCircle = 0f
                buttonTitle = resources.getString(R.string.button_download)
                invalidate()
            }
        }
    }
    private var btnColor: Int = 0
    private var btnLoadingColor: Int = 0
    private var btnCompleteColor: Int = 0

    private val paint = Paint().apply {
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    init {
        buttonTitle = resources.getString(R.string.button_download)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            btnColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            btnLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            btnCompleteColor = getColor(R.styleable.LoadingButton_buttonCompletedColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas)
        drawAnimatedColor(canvas)
        drawTitle(canvas)
        drawAnimatedCircle(canvas)
    }

    private fun drawAnimatedCircle(canvas: Canvas?) {
        canvas?.save()
        paint.color = resources.getColor(R.color.colorAccent)
        val textWidth = paint.measureText(buttonTitle)
        canvas?.translate(
            (widthSize / 2) + (textWidth / 2) + (textSize / 2), (height / 2) - (textSize / 2)
        )
        canvas?.drawArc(
            RectF(0f, 0f, textSize, textSize), 0F, animateCircle * 0.365f, true, paint
        )
        canvas?.restore()
    }

    private fun drawBackgroundColor(canvas: Canvas?) {
        paint.color = btnColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawTitle(canvas: Canvas?) {
        paint.color = Color.WHITE
        val textWidth = paint.measureText(buttonTitle)
        canvas?.drawText(
            buttonTitle,
            ((widthSize / 2) - (textWidth / 2)),
            ((heightSize / 2) - (paint.descent() + paint.ascent()) / 2),
            paint
        )
    }

    private fun drawAnimatedColor(canvas: Canvas?) {
        paint.color = btnLoadingColor
        canvas?.drawRect(0f, 0f, animateWidth, heightSize.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w), heightMeasureSpec, 0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}