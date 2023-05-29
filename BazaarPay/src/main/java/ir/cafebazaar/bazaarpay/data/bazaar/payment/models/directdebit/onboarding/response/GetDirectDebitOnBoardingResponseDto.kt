package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.ThemedIcon

internal class GetDirectDebitOnBoardingResponseDto(
    @SerializedName("details") val onBoardingDetails: List<OnBoardingItemDto>
) {

    fun toDirectDebitOnBoardingDetails(): DirectDebitOnBoardingDetails {
        return DirectDebitOnBoardingDetails(onBoardingDetails.map { it.toOnBoardingItem() })
    }
}

internal class OnBoardingItemDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: ThemedIconDto?
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
    @SerializedName("lightIcon") val lightIcon: String?,
    @SerializedName("darkIcon") val darkIcon: String?
) {

    fun toThemedIcon(): ThemedIcon {
        return ThemedIcon(
            lightIcon,
            darkIcon
        )
    }
}