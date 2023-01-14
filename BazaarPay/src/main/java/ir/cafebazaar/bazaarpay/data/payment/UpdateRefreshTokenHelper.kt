package ir.cafebazaar.bazaarpay.data.payment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UpdateRefreshTokenHelper : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    var needToUpdateRefreshToken = true
        private set

    fun onRefreshTokenUpdated() {
        needToUpdateRefreshToken = false
        launch {
            delay(REFRESH_TOKEN_UPDATE_DELAY)
            needToUpdateRefreshToken = true
        }
    }

    companion object {
        private const val REFRESH_TOKEN_UPDATE_DELAY = 5_000L
    }
}