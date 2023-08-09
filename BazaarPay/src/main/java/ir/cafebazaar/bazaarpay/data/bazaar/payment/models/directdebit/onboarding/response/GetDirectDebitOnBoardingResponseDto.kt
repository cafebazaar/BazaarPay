package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingHeader
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.OnBoardingItem
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.ThemedIcon

internal class GetDirectDebitOnBoardingResponseDto(
    @SerializedName("default_detail") val header: DirectDebitOnBoardingHeaderDto,
    @SerializedName("details") val onBoardingDetails: List<OnBoardingItemDto>
) {

    fun toDirectDebitOnBoardingDetails(): DirectDebitOnBoardingDetails {
        return DirectDebitOnBoardingDetails(
            header.toDirectDebitOnBoardingHeader(),
            onBoardingDetails.map { it.toOnBoardingItem() }
        )
    }
}

internal class DirectDebitOnBoardingHeaderDto(
    @SerializedName("description") val title: String,
    @SerializedName("title") val description: String,
    @SerializedName("icon") val icon: ThemedIconDto?
) {

    fun toDirectDebitOnBoardingHeader(): DirectDebitOnBoardingHeader {
        return DirectDebitOnBoardingHeader(
            title,
            description,
            icon?.toThemedIcon()
        )
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
    @SerializedName("light_icon") val lightIcon: String?,
    @SerializedName("dark_icon") val darkIcon: String?
) {

    fun toThemedIcon(): ThemedIcon {
        return ThemedIcon(
            lightIcon,
            darkIcon
        )
    }
}