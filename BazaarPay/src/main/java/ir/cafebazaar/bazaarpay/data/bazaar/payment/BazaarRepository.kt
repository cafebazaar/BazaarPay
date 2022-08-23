package ir.cafebazaar.bazaarpay.data.bazaar.payment

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.onboarding.DirectDebitOnBoardingDetails
import ir.cafebazaar.bazaarpay.utils.Either

internal class BazaarRepository {

    private val bazaarRemoteDataSource: BazaarRemoteDataSource = ServiceLocator.get()

    fun getDirectDebitOnBoarding(): Either<DirectDebitOnBoardingDetails> {
        return bazaarRemoteDataSource.getDirectDebitOnBoarding()
    }

    fun getAvailableBanks(): Either<AvailableBanks> {
        return bazaarRemoteDataSource.getAvailableBanks()
    }

    fun getDirectDebitContractCreationUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return bazaarRemoteDataSource.getCreateContractUrl(
            bankCode,
            nationalId
        )
    }

    fun activatePostPaidCredit(): Either<Unit> {
        return bazaarRemoteDataSource.activatePostPaid()
    }
}