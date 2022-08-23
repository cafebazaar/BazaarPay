package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.ThemedIcon

internal class DirectDebitOnBoardingSingleReply(
    val singleReply: DirectDebitOnBoardingReply
) : BazaarBaseResponse()

internal data class DirectDebitOnBoardingReply(
    val getDirectDebitOnBoardingReply: DirectDebitOnBoardingReplyBody
)

internal data class DirectDebitOnBoardingReplyBody(
    val onBoardingDetails: List<OnBoardingItemDto>
) {

    fun toDirectDebitOnBoardingDetails(): DirectDebitOnBoardingDetails {
        return DirectDebitOnBoardingDetails(onBoardingDetails.map { it.toOnBoardingItem() })
    }
}

internal data class OnBoardingItemDto(
    val title: String,
    val description: String,
    val icon: ThemedIconDto?
) {

    fun toOnBoardingItem(): OnBoardingItem {
        return OnBoardingItem(
            title,
            description,
            icon?.toThemedIcon()
        )
    }
}

internal class ThemedIconDto(
    val lightIcon: String?,
    val darkIcon: String?
) {

    fun toThemedIcon(): ThemedIcon {
        return ThemedIcon(
            lightIcon,
            darkIcon
        )
    }
}