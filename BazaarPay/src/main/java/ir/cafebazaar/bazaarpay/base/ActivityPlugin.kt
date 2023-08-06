package ir.cafebazaar.bazaarpay.base

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver

internal interface ActivityPlugin : DefaultLifecycleObserver {

    fun onCreate(savedInstanceState: Bundle?) {}
}