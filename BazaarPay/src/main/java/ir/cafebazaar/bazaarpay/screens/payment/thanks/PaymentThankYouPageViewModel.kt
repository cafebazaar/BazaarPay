package ir.cafebazaar.bazaarpay.screens.payment.thanks

import android.os.CountDownTimer
import android.text.format.DateUtils.SECOND_IN_MILLIS
import androidx.databinding.adapters.SeekBarBindingAdapter.setProgress
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ir.cafebazaar.bazaarpay.R
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent


internal class PaymentThankYouPageViewModel : ViewModel() {

    private val _viewStateLiveData = SingleLiveEvent<Resource<PaymentThankYouPageSuccessModel>>()
    val viewStateLiveData: LiveData<Resource<PaymentThankYouPageSuccessModel>> = _viewStateLiveData

    private val _performSuccessLiveData = SingleLiveEvent<Unit>()
    val performSuccessLiveData: LiveData<Unit> = _performSuccessLiveData

    var countDownTimer: CountDownTimer? = null

    fun onDataReceived(args: PaymentThankYouPageFragmentArgs) {
        if (args.isSuccess) {
            handlePaymentSuccess(args)
        } else {
            _viewStateLiveData.value = Resource.failed(failure = args.errorModel)
        }
    }

    private fun handlePaymentSuccess(args: PaymentThankYouPageFragmentArgs) {
        runTimerForSuccessMessage(args)
    }

    private fun runTimerForSuccessMessage(args: PaymentThankYouPageFragmentArgs) {
        countDownTimer = object : CountDownTimer(
            COUNT_DOWN_TIMER_SEC * SECOND_IN_MILLIS,
            SECOND_IN_MILLIS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val progressPercent = 100 - ((millisUntilFinished * 100) / (COUNT_DOWN_TIMER_SEC * SECOND_IN_MILLIS))

                _viewStateLiveData.value = Resource.loaded(
                    data = PaymentThankYouPageSuccessModel(
                        paymentProgressBarModel = PaymentProgressBarModel(
                            successMessageCountDown = progressPercent,
                            seconds = (millisUntilFinished / SECOND_IN_MILLIS).toInt().plus(1)
                        ),
                        messageTextModel = PaymentThankYouPageSuccessMessageModel(
                            argMessage = args.message,
                            defaultMessageId = R.string.bazaarpay_payment_done_message
                        )
                    )
                )
            }

            override fun onFinish() {
                _performSuccessLiveData.call()
            }
        }.start()
    }

    override fun onCleared() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onCleared()
    }

    companion object {

        private const val COUNT_DOWN_TIMER_SEC = 5
    }
}