package ir.cafebazaar.bazaarpay.utils

import android.content.Context
import ir.cafebazaar.bazaarpay.ServiceLocator

fun getAppVersionName(): String {
    return ServiceLocator.getOrNull<Context>()?.packageManager
        ?.getPackageInfo(ServiceLocator.get<Context>().packageName, 0)?.versionName.orEmpty()
}