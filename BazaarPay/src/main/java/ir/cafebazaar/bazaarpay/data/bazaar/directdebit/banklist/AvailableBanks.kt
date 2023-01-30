package ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.ThemedIcon

internal data class AvailableBanks(
    val banks: List<Bank>
)

internal data class Bank(
    val code: String,
    val name: String,
    val icon: ThemedIcon,
    val description: String
)