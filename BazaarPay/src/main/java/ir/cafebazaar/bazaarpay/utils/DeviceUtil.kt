package ir.cafebazaar.bazaarpay.utils

import android.content.res.Resources
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import ir.cafebazaar.bazaarpay.R

fun TextView.setFont(@FontRes font: Int = R.font.yekanbakh_regular) {
    if (!isInEditMode) {
        try {
            typeface = ResourcesCompat.getFont(context, font)
        } catch (exception: Resources.NotFoundException) {
        }
    }
}
