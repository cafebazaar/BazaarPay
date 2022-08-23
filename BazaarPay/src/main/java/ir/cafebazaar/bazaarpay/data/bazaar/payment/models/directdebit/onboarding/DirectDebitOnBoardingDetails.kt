package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding

import ir.cafebazaar.bazaarpay.ServiceLocator

internal class DirectDebitOnBoardingDetails(
    val onBoardingDetails: List<OnBoardingItem>
)

internal data class OnBoardingItem(
    val title: String,
    val description: String,
    val icon: ThemedIcon?
) {

    fun getImageUri(): String {
        return icon?.getImageUriFromThemedIcon().orEmpty()
    }
}

internal class ThemedIcon(
    val lightIcon: String?,
    val darkIcon: String?
) {

    fun getImageUriFromThemedIcon(): String {
        return if (ServiceLocator.get(ServiceLocator.IS_DARK)) {
            darkIcon.orEmpty()
        } else {
            lightIcon.orEmpty()
        }
    }
}