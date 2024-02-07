package ir.cafebazaar.bazaarpay.data.payment.models.warning

import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.ThemedIcon

internal data class Warning(
    val text: String,
    val icon: ThemedIcon,
    val actionText: String,
)
