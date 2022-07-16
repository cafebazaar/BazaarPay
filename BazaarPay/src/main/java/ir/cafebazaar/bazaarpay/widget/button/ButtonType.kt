package ir.cafebazaar.bazaarpay.widget.button

import ir.cafebazaar.bazaarpay.R

internal enum class ButtonType(
    val color: Int
) {

    APP(
        R.color.app_brand_primary
    ),
    VIDEO(
        R.color.video_brand_primary
    ),
    NEUTRAL(
        R.color.grey_90
    ),
    DISABLED(
        R.color.grey_20
    )
}