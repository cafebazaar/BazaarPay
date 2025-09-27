package ir.cafebazaar.bazaarpay.sample.utils.extensions

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

internal fun View.applyWindowInsets(
    @WindowInsetsCompat.Type.InsetsType typeMask: Int
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val innerPadding =
            insets.getInsets(typeMask)
        view.setPadding(
            innerPadding.left,
            innerPadding.top,
            innerPadding.right,
            innerPadding.bottom
        )
        insets
    }
}

internal fun View.applyWindowInsetsWithoutTop(
    @WindowInsetsCompat.Type.InsetsType typeMask: Int
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val innerPadding =
            insets.getInsets(typeMask)
        view.setPadding(
            innerPadding.left,
            0,
            innerPadding.right,
            innerPadding.bottom
        )
        insets
    }
}