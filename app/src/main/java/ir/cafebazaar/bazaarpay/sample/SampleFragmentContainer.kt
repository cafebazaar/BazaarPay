package ir.cafebazaar.bazaarpay.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.cafebazaar.bazaarpay.sample.utils.extensions.enableEdgeToEdge

class SampleFragmentContainer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
    }
}
