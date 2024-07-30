package ir.cafebazaar.bazaarpay.screens.login.verify

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateUtils.SECOND_IN_MILLIS
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.cafebazaar.bazaarpay.ServiceLocator
import ir.cafebazaar.bazaarpay.data.bazaar.account.AccountRepository
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.getotptoken.WaitingTimeWithEnableCall
import ir.cafebazaar.bazaarpay.data.bazaar.account.models.verifyotptoken.LoginResponse
import ir.cafebazaar.bazaarpay.data.bazaar.models.ErrorModel
import ir.cafebazaar.bazaarpay.extensions.fold
import ir.cafebazaar.bazaarpay.extensions.getFailureOrNull
import ir.cafebazaar.bazaarpay.extensions.getOrNull
import ir.cafebazaar.bazaarpay.models.Resource
import ir.cafebazaar.bazaarpay.models.ResourceState
import ir.cafebazaar.bazaarpay.models.VerificationState
import ir.cafebazaar.bazaarpay.utils.Either
import ir.cafebazaar.bazaarpay.utils.SingleLiveEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.properties.Delegates

internal class VerifyOtpViewModel : ViewModel() {

    private val accountRepository: AccountRepository by lazy { ServiceLocator.get() }

    private var remainingWaitingTime: Long? = null
    private var countDownTimer: CountDownTimer? = null

    var showCall: Boolean by Delegates.notNull()

    private val _verifyCodeStateLiveData = MutableLiveData<Resource<Nothing>>()
    val verifyCodeStateLiveData: LiveData<Resource<Nothing>> = _verifyCodeStateLiveData

    private val _verificationCodeLiveData = SingleLiveEvent<String>()
    val verificationCodeLiveData: LiveData<String> = _verificationCodeLiveData

    private val _resendSmsAndCallLiveData = SingleLiveEvent<Resource<Long>>()
    val resendSmsAndCallLiveData: LiveData<Resource<Long>> = _resendSmsAndCallLiveData

    private val _showCallButtonLiveData = SingleLiveEvent<Boolean>()
    val showCallButtonLiveData: LiveData<Boolean> = _showCallButtonLiveData

    private val _onStartSmsPermissionLiveData = SingleLiveEvent<Intent>()
    val onStartSmsPermissionLiveData: LiveData<Intent> = _onStartSmsPermissionLiveData

    fun onCreate(
        waitingTimeWithEnableCall: WaitingTimeWithEnableCall,
        savedInstanceState: Bundle?
    ) {
        val remainingTime = remainingWaitingTime
            ?: savedInstanceState?.getLong(ARG_REMAINING_WAITING_TIME)
            ?: waitingTimeWithEnableCall.seconds
        showCall = waitingTimeWithEnableCall.isCallEnabled

        if (remainingTime != 0L) {
            startCountDown(remainingTime)
        }
    }

    private fun startCountDown(
        remainingTime: Long,
        resourceState: ResourceState = ResourceState.Success,
        throwable: ErrorModel? = null
    ) {

        remainingWaitingTime = max(remainingTime, MINIMUM_WAITING_TIME)
        remainingWaitingTime?.also {

            _resendSmsAndCallLiveData.value = Resource(resourceState, it, failure = throwable)

            // cancel the previous countDownTimer if any set
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(it * SECOND_IN_MILLIS, SECOND_IN_MILLIS) {
                override fun onTick(millisUntilFinished: Long) {

                    // convert to seconds
                    remainingWaitingTime = millisUntilFinished / ONE_SEC_IN_MILLI_SECOND
                    _resendSmsAndCallLiveData.value = Resource(
                        VerificationState.Tick,
                        remainingWaitingTime
                    )
                }

                override fun onFinish() {
                    remainingWaitingTime = 0
                    _resendSmsAndCallLiveData.value = Resource(VerificationState.FinishCountDown)
                    _showCallButtonLiveData.value = showCall
                }
            }
            countDownTimer?.start()
        }
    }

    fun verifyCode(phoneNumber: String, code: String) {
        if (_verifyCodeStateLiveData.value?.isLoading == true) {
            return
        }

        _verifyCodeStateLiveData.value = Resource.loading()
        viewModelScope.launch {
            handleVerifyCodeResponse(accountRepository.verifyOtpToken(phoneNumber, code))
        }
    }

    private fun handleVerifyCodeResponse(response: Either<LoginResponse>) {
        response.getOrNull()?.let {
            _verifyCodeStateLiveData.value = Resource.loaded()
        } ?: run {
            verifyCodeError(response.getFailureOrNull() ?: ErrorModel.UnExpected)
        }
    }

    private fun verifyCodeError(throwable: ErrorModel) {
        _verifyCodeStateLiveData.value = Resource.failed(failure = throwable)
    }

    fun onResendSmsClicked(phoneNumber: String) {
        viewModelScope.launch {
            _resendSmsAndCallLiveData.value = Resource.loading()
            accountRepository.getOtpToken(phoneNumber)
                .fold(
                    ifSuccess = {
                        startCountDown(it.seconds)
                    },
                    ifFailure = { throwable ->
                        startCountDown(MINIMUM_WAITING_TIME, ResourceState.Error, throwable)
                    }
                )
        }
    }

    fun onCallButtonClicked(phoneNumber: String) {
        viewModelScope.launch {

            _resendSmsAndCallLiveData.value = Resource.loading()
            accountRepository.getOtpTokenByCall(phoneNumber)
                .fold(
                    ifSuccess = {
                        showCall = false
                        startCountDown(it.value)
                    },
                    ifFailure = { throwable ->
                        startCountDown(MINIMUM_WAITING_TIME, ResourceState.Error, throwable)
                    }
                )
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        remainingWaitingTime?.also {
            outState.putLong(ARG_REMAINING_WAITING_TIME, it)
        }
    }

    override fun onCleared() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    @FlowPreview
    fun onActivityCreated() {
        startReceiveSms()
    }

    @FlowPreview
    private fun startReceiveSms() {
        viewModelScope.launch {
            accountRepository.onSmsPermissionSharedFlow.collect {
                _onStartSmsPermissionLiveData.value = it
            }
        }
    }

    fun onSmsMessage(oneTimeSMSMessage: String) {
        val oneTimeCode = oneTimeSMSMessage.filter {
            it.isDigit()
        }.take(4)
        _verificationCodeLiveData.value = oneTimeCode
    }

    companion object {

        private const val ARG_REMAINING_WAITING_TIME = "remainingWaitingTime"
        private const val MINIMUM_WAITING_TIME = 5L
        private const val ONE_SEC_IN_MILLI_SECOND = 1000
    }
}