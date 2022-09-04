package ir.cafebazaar.bazaarpay.data.bazaar.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.utils.Either

internal class BazaarPaymentRepository {

    private val bazaarPaymentRemoteDataSource: BazaarPaymentRemoteDataSource = ServiceLocator.get()

    suspend fun getDirectDebitOnBoarding(): Either<DirectDebitOnBoardingDetails> {
        return bazaarPaymentRemoteDataSource.getDirectDebitOnBoarding()
    }

    suspend fun getAvailableBanks(): Either<AvailableBanks> {
        return bazaarPaymentRemoteDataSource.getAvailableBanks()
    }

    suspend fun getDirectDebitContractCreationUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return bazaarPaymentRemoteDataSource.getCreateContractUrl(
            bankCode,
            nationalId
        )
    }

    suspend fun activatePostPaidCredit(): Either<Unit> {
        return bazaarPaymentRemoteDataSource.activatePostPaid()
    }
}