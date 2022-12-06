package ir.cafebazaar.bazaarpay.widget.button

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.TextUtils
import android.util.AttributeSet
import android.util.StateSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.extensions.gone
import ir.cafebazaar.bazaarpay.extensions.visible
import ir.cafebazaar.bazaarpay.utils.setFont
import ir.cafebazaar.bazaarpay.widget.loading.SpinKitView
import java.util.Arrays

internal class BazaarPayButton : LinearLayout {

    private val buttonCornerRadius = resources.getDimension(
        R.dimen.bazaar_button_corner_radius
    )
    private val buttonStrokeWidth = resources.getDimensionPixelOffset(
        R.dimen.bazaar_button_stroke_width
    )

    private var textView: AppCompatTextView = AppCompatTextView(context).apply {
        setFont()
        gravity = Gravity.CENTER
        maxLines = 1
        visibility = View.VISIBLE
        ellipsize = TextUtils.TruncateAt.END
    }
    private var loadingView: SpinKitView? = null
    private var rightIcon: ImageView? = null

    private val loadingSize = resources.getDimensionPixelOffset(
        R.dimen.bazaar_button_loading_size
    )
    private val iconSize = resources.getDimensionPixelOffset(
        R.dimen.bazaar_button_icon_size
    )
    private val margin = resources.getDimensionPixelOffset(
        R.dimen.bazaar_button_margin
    )

    var text: CharSequence? = ""
        set(value) {
            field = value
            textView.text = value
        }

    var style = ButtonStyle.CONTAINED
        set(value) {
            field = value
            render()
        }

    var type = ButtonType.APP
        set(value) {
            field = value
            render()
        }

    var buttonSize = ButtonSize.MEDIUM

    @ColorInt
    var strokeColor: Int? = null
        set(value) {
            field = value
            render()
        }

    @ColorInt
    var rippleColor: Int? = null
        set(value) {
            field = value
            render()
        }

    var isLoading: Boolean
        get() = loadingView?.visibility == VISIBLE
        set(value) {
            handleLoading(show = value)
        }

    var rightIconResId: Int? = null
        set(value) {
            field = value
            if (value != null) {
                rightIcon?.setImageDrawable(ContextCompat.getDrawable(context, value))
                rightIcon?.visible()
            } else {
                rightIcon?.gone()
            }
            render()
        }

    constructor(context: Context) : super(context) {
        initSelf()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BazaarButton)
        initSelf(typedArray)
        typedArray.recycle()
    }

    private fun initSelf(typedArray: TypedArray? = null) {
        isClickable = true
        setPadding(margin, 0, margin, 0)
        typedArray?.let {
            style = ButtonStyle.values()[
                it.getInt(R.styleable.BazaarButton_bazaarpayButtonTheme, ButtonStyle.CONTAINED.ordinal)
            ]
            type = ButtonType.values()[
                it.getInt(R.styleable.BazaarButton_bazaarpayButtonType, ButtonType.APP.ordinal)
            ]
            rightIconResId = it.getResourceId(R.styleable.BazaarButton_rightIcon, UNDEFINED)
            buttonSize = ButtonSize.values()[
                it.getInt(R.styleable.BazaarButton_sizeMode, ButtonSize.MEDIUM.ordinal)
            ]
        }
        gravity = Gravity.CENTER
        initIcon()
        initTextView(typedArray)
        initLoadingView(typedArray)
        minimumWidth = resources.getDimensionPixelSize(buttonSize.minWidth)
        if (type == ButtonType.DISABLED) {
            isEnabled = false
        }
        render()
    }

    private fun initIcon() {
        rightIconResId?.takeUnless { it == UNDEFINED }?.let { iconRes ->
            val params = LayoutParams(iconSize, iconSize)
            rightIcon = ImageView(context).apply {
                id = ViewCompat.generateViewId()
                setImageResource(iconRes)
            }
            addView(rightIcon, params)
        }
    }

    private fun initTextView(typedArray: TypedArray?) {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            rightMargin = margin
            leftMargin = margin
        }
        addView(textView, params)
        text = typedArray?.getString(R.styleable.BazaarButton_text).orEmpty()
    }

    private fun initLoadingView(typedArray: TypedArray?) {
        val params = LayoutParams(loadingSize, loadingSize).apply {
            rightMargin = margin
            leftMargin = margin
        }
        loadingView = SpinKitView(context)
        val showLoading = typedArray?.getBoolean(
            R.styleable.BazaarButton_showLoading, false
        ) ?: false
        handleLoading(showLoading)
        addView(loadingView, params)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.height = resources.getDimensionPixelOffset(buttonSize.buttonHeight)
        super.setLayoutParams(params)
    }

    private fun render() {
        val textStyle = if (buttonSize == ButtonSize.LARGE) {
            R.style.Bazaar_Text_Subtitle1
        } else {
            R.style.Bazaar_Text_Subtitle2
        }
        TextViewCompat.setTextAppearance(textView, textStyle)
        val textColorStateList = getButtonContentColor()
        textView.setTextColor(textColorStateList)
        loadingView?.setColor(textColorStateList.defaultColor)
        background = when (style) {
            ButtonStyle.CONTAINED -> {
                createLayerList(ContextCompat.getColor(context, type.color))
            }
            ButtonStyle.OUTLINE -> {
                createLayerList(
                    ContextCompat.getColor(context, R.color.grey_10),
                    ContextCompat.getColor(context, R.color.grey_40)
                )
            }
            ButtonStyle.CONTAINED_GREY -> {
                createLayerList(ContextCompat.getColor(context, R.color.grey_20))
            }
            ButtonStyle.TRANSPARENT -> {
                createLayerList(Color.TRANSPARENT)
            }
        }
        invalidate()
    }

    fun setText(@StringRes text: Int) {
        textView.setText(text)
        handleTextViewVisibility(show = true)
        handleLoading(show = false)
    }

    fun setTextColor(textColor: Int) {
        textView.setTextColor(textColor)
    }

    private fun handleLoading(show: Boolean = true) {
        isClickable = show.not()
        if (show) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    private fun showLoading() {
        loadingView?.visible()
        handleTextViewVisibility(show = false)
        rightIcon?.gone()
    }

    private fun hideLoading() {
        loadingView?.gone()
        handleTextViewVisibility(show = true)
        if (rightIconResId != null) {
            rightIcon?.visible()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        textView.isEnabled = enabled
        rightIcon?.isEnabled = enabled
    }

    private fun createButtonContained(
        @ColorInt primaryColor: Int,
        @ColorInt strokeColor: Int? = null
    ): Drawable {
        val enabledStateDrawable = GradientDrawable().apply {
            cornerRadius = buttonCornerRadius
            colors = intArrayOf(primaryColor, primaryColor, primaryColor)
            strokeColor?.let {
                setStroke(buttonStrokeWidth, strokeColor)
            }
        }
        val disabledStateDrawable = GradientDrawable().apply {
            cornerRadius = buttonCornerRadius
            val disableColor = ContextCompat.getColor(context, R.color.grey_20)
            colors = intArrayOf(disableColor, disableColor, disableColor)
        }
        return StateListDrawable().apply {
            addState(
                intArrayOf(android.R.attr.state_enabled),
                enabledStateDrawable
            )
            addState(
                StateSet.WILD_CARD,
                disabledStateDrawable
            )
        }
    }

    private fun createButtonRipple(@ColorInt color: Int): Drawable {
        val rippleColor = ContextCompat.getColor(context, R.color.ripple_color)
        return RippleDrawable(
            ColorStateList.valueOf(rippleColor), null, getRippleMask(color)
        )
    }

    private fun getRippleMask(@ColorInt color: Int): Drawable {
        val outerRadius = FloatArray(RIPPLE_OUT_RADIUS_ARRAY_SIZE)
        Arrays.fill(outerRadius, buttonCornerRadius)
        val roundRectShape = RoundRectShape(outerRadius, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape)
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    private fun createLayerList(
        @ColorInt primaryColor: Int,
        @ColorInt strokeColor: Int? = this.strokeColor,
        @ColorInt rippleColor: Int? = this.rippleColor,
    ): StateListDrawable {
        val finalRippleColor = rippleColor ?: primaryColor
        val shapeDrawable = createButtonContained(primaryColor, strokeColor)
        val rippleDrawable = createButtonRipple(finalRippleColor)
        val layers = arrayOf(shapeDrawable, rippleDrawable)
        val buttonWithRippleLayerList = LayerDrawable(layers)
        return StateListDrawable().apply {
            addState(
                intArrayOf(android.R.attr.state_pressed),
                buttonWithRippleLayerList
            )
            addState(
                StateSet.WILD_CARD,
                shapeDrawable
            )
        }
    }

    private fun getButtonContentColor(): ColorStateList {
        val enabledStateColorRes = when (style.contentColor) {
            ButtonContentColorType.GREY -> {
                when (type) {
                    ButtonType.NEUTRAL -> {
                        R.color.grey_90
                    }
                    else -> {
                        R.color.grey_10
                    }
                }
            }
            ButtonContentColorType.BUTTON_TYPE_COLOR -> type.color
        }
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                StateSet.WILD_CARD
            ),
            intArrayOf(
                ContextCompat.getColor(context, enabledStateColorRes),
                ContextCompat.getColor(context, R.color.grey_60)
            )
        )
    }

    private fun handleTextViewVisibility(show: Boolean) {
        val isIconButton = rightIcon?.visibility == VISIBLE && textView.text.isNullOrEmpty()
        textView.isVisible = show && isIconButton.not()
    }

    private companion object {

        const val UNDEFINED = 0
        const val RIPPLE_OUT_RADIUS_ARRAY_SIZE = 8
    }
}