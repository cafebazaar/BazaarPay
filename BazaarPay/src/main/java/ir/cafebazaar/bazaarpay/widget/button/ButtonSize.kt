package ir.cafebazaar.bazaarpay.widget.button

import androidx.annotation.DimenRes
import ir.cafebazaar.bazaarpay.R

internal enum class ButtonSize(
    @DimenRes val buttonHeight: Int,
    @DimenRes val minWidth: Int
) {

    MEDIUM(
        R.dimen.bazaar_button_medium_height,
        R.dimen.bazaarpay_medium_button_width
    ),
    LARGE(
        R.dimen.bazaar_button_large_height,
        R.dimen.bazaarpay_medium_button_width
    ),
    SMALL(
        R.dimen.bazaar_button_small_height,
        R.dimen.bazaarpay_small_button_width
    )
}