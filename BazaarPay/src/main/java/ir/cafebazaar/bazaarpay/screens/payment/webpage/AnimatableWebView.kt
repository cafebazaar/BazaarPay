package ir.cafebazaar.bazaarpay.screens.payment.webpage

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.animation.doOnEnd
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class AnimatableWebView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attributeSet, defStyleAttr) {

    var animationListener: (Float) -> Unit = {}
        set(value) {
            field = value
            value(fraction)
        }

    private var initialScrollY: Int = 0
    private var initialFraction: Float = 0f
    private var valueAnimator: ValueAnimator? = null

    private var fraction: Float = 1f
        set(value) {
            field = value
            animationListener(value)
        }
    private var isUrlBarVisible = fraction == 1f

    private val animationEnabled: Boolean = true

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (animationEnabled.not()) {
            return super.dispatchTouchEvent(event)
        }

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialScrollY = scrollY
                initialFraction = fraction
                cancelAnimation()
            }
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_SCROLL -> {
                if (valueAnimator == null && animationEnabled) {
                    val dy = scrollY - initialScrollY
                    if (dy.absoluteValue >= MIN_SCROLL) {
                        if (isUrlBarVisible.not() && dy <= 0) {
                            fraction = calculateFraction(dy)
                        } else if (isUrlBarVisible && dy >= 0) {
                            fraction = initialFraction - calculateFraction(dy)
                        }
                    }
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                animate(fraction >= 0.5f)
            }
        }
        super.dispatchTouchEvent(event)
        return true
    }

    private fun cancelAnimation() {
        valueAnimator?.cancel()
        valueAnimator = null
    }

    private fun animate(
        visible: Boolean,
        currentFraction: Float = fraction
    ) {
        cancelAnimation()
        isUrlBarVisible = visible
        val target = if (visible) {
            1f
        } else {
            0f
        }

        if (currentFraction == target) {
            valueAnimator = null
            return
        }
        valueAnimator = ValueAnimator.ofFloat(
            currentFraction,
            target
        ).apply {
            duration = 200
            addUpdateListener {
                fraction = it.animatedValue as Float
            }
            doOnEnd {
                valueAnimator = null
            }
            start()
        }
    }

    private fun calculateFraction(y: Int): Float {
        val value = (y.absoluteValue - MIN_SCROLL) / (50 * resources.displayMetrics.density)
        return max(0f, min(1f, value))
    }

    private companion object {

        const val MIN_SCROLL = 50
    }
}