package ir.cafebazaar.bazaarpay.data.bazaar.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.utils.Either

internal class BazaarRepository {

    private val bazaarRemoteDataSource: BazaarRemoteDataSource = ServiceLocator.get()

    suspend fun getDirectDebitOnBoarding(): Either<DirectDebitOnBoardingDetails> {
        return bazaarRemoteDataSource.getDirectDebitOnBoarding()
    }

    suspend fun getAvailableBanks(): Either<AvailableBanks> {
        return bazaarRemoteDataSource.getAvailableBanks()
    }

    suspend fun getDirectDebitContractCreationUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return bazaarRemoteDataSource.getCreateContractUrl(
            bankCode,
            nationalId
        )
    }

    suspend fun activatePostPaidCredit(): Either<Unit> {
        return bazaarRemoteDataSource.activatePostPaid()
    }
}