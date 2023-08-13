package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding

import android.content.Context
import ir.cafebazaar.bazaarpay.utils.isDarkMode

internal class DirectDebitOnBoardingDetails(
    val header: DirectDebitOnBoardingHeader,
    val onBoardingDetails: List<OnBoardingItem>
)

internal data class OnBoardingItem(
    val title: String,
    val description: String,
    val icon: ThemedIcon?
)

internal data class DirectDebitOnBoardingHeader(
    val title: String,
    val description: String,
    val icon: ThemedIcon?
)

internal class ThemedIcon(
    val lightIcon: String?,
    val darkIcon: String?
) {

    fun getImageUriFromThemedIcon(context: Context): String {
        return if (isDarkMode(context)) {
            darkIcon.orEmpty()
        } else {
            lightIcon.orEmpty()
        }
    }
}