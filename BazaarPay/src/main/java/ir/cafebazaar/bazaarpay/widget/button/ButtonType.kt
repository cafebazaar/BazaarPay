package ir.cafebazaar.bazaarpay.widget.button

import ir.cafebazaar.bazaarpay.R

internal enum class ButtonType(
    val color: Int
) {

    APP(
        R.color.bazaarpay_app_brand_primary
    ),
    NEUTRAL(
        R.color.bazaarpay_grey_90
    ),
    DISABLED(
        R.color.bazaarpay_grey_20
    )
}