package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.response

import ir.cafebazaar.bazaarpay.data.bazaar.models.BazaarBaseResponse
import ir.cafebazaar.bazaarpay.extensions.persianDigitsIfPersian
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.Bank
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.response.ThemedIconDto
import java.util.*

internal class GetAvailableBanksSingleReply(
    val singleReply: GetAvailableBanksReply
) : BazaarBaseResponse()

internal data class GetAvailableBanksReply(
    val getAvailableBanksReply: GetAvailableBanksReplyBody
)

internal data class GetAvailableBanksReplyBody(
    val banks: List<BankDto>
) {

    fun toAvailableBanks(): AvailableBanks {
        return AvailableBanks(banks.map { it.toBank() })
    }
}

internal class BankDto(
    val code: String,
    val name: String,
    val icon: ThemedIconDto,
    val description: String
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