package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response

import com.google.gson.annotations.SerializedName
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.Bank
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.ThemedIconDto
import ir.cafebazaar.bazaarpay.extensions.persianDigitsIfPersian
import java.util.*

internal data class GetAvailableBanksResponseDto(
    @SerializedName("banks") val banks: List<BankDto>
) {

    fun toAvailableBanks(): AvailableBanks {
        return AvailableBanks(banks.map { it.toBank() })
    }
}

internal class BankDto(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: ThemedIconDto,
    @SerializedName("description") val description: String
) {

    fun toBank(): Bank {
        return Bank(
            code,
            name,
            icon.toThemedIcon(),
            description.persianDigitsIfPersian(Locale.getDefault())
        )
    }
}