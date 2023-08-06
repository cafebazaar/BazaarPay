package ir.cafebazaar.bazaarpay.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver

internal interface FragmentPlugin : DefaultLifecycleObserver {

    fun onAttach(context: Context) {}

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    fun onDestroyView(fragment: Fragment) {}
}