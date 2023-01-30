package ir.cafebazaar.bazaarpay.data.bazaar.directdebit

import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.directdebit.contractcreation.ContractCreation
import ir.cafebazaar.bazaarpay.data.bazaar.payment.models.directdebit.banklist.AvailableBanks
import ir.cafebazaar.bazaarpay.utils.Either

internal class DirectDebitRepository {

    private val directDebitRemoteDataSource: DirectDebitRemoteDataSource = ServiceLocator.get()

    suspend fun getDirectDebitContractCreationUrl(
        bankCode: String,
        nationalId: String
    ): Either<ContractCreation> {
        return directDebitRemoteDataSource.getCreateContractUrl(
            bankCode,
            nationalId
        )
    }

    suspend fun getAvailableBanks(): Either<AvailableBanks> {
        return directDebitRemoteDataSource.getAvailableBanks()
    }
}