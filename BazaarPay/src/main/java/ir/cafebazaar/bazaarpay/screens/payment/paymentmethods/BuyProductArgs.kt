package ir.cafebazaar.bazaarpay.screens.payment.paymentmethods

import android.os.Parcel
import android.os.Parcelable

internal class BuyProductArgs(
    val dealerPackageName: String,
    val sku: String,
    val paymentType: String,
    val developerPayload: String? = null,
    val dynamicPriceToken: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dealerPackageName)
        parcel.writeString(sku)
        parcel.writeString(paymentType)
        parcel.writeString(developerPayload)
        parcel.writeString(dynamicPriceToken)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<BuyProductArgs> {
        override fun createFromParcel(parcel: Parcel): BuyProductArgs = BuyProductArgs(parcel)
        override fun newArray(size: Int): Array<BuyProductArgs?> = arrayOfNulls(size)
    }
}