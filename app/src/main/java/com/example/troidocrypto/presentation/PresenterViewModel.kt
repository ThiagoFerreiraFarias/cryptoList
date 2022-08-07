package com.example.troidocrypto.presentation

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.troidocrypto.domain.data.CoinBaseUpdate
import com.example.troidocrypto.domain.usecase.GetRateUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

const val MAX_CARD_DISPLAYED = 50
const val REQUEST_TIME_GAP = 60000L
const val MAX_CALLBACK_ATTEMPTS = 3
const val MIN_CALLBACK_ATTEMPTS_ACCEPTED = 0
const val POP_UP_INDEX_ON_LIST_MAX_SIZE = 0
const val ERROR_TAG = "API_EXCEPTION"

class PresenterViewModel : ViewModel() {

    private val useCase = GetRateUseCase()
    private lateinit var view: View
    private var ratesUpdateList = mutableListOf<CoinBaseUpdate>()
    val ratesUpdate = ObservableField<MutableList<CoinBaseUpdate>>()
    private var continuousRequest: Disposable? = null
    private var updateRequest: Disposable? = null
    private var callbackAttempts = MIN_CALLBACK_ATTEMPTS_ACCEPTED

    fun initSetup(view: View) {
        this.view = view
        ratesUpdate.set(ratesUpdateList)
        verifyRequestStatus()
    }

    fun verifyRequestStatus() {
        if(callbackAttempts == MIN_CALLBACK_ATTEMPTS_ACCEPTED) {
            callbackAttempts = MAX_CALLBACK_ATTEMPTS
            repeatRequest()
            getRate()
        }
    }

    private fun repeatRequest() {
        continuousRequest = Observable.interval(REQUEST_TIME_GAP, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getRate() }
    }

    private fun getRate() {
        try {
            updateRequest = useCase.getRateUpdate()
                .doOnError {
                    handleError(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(::handleResults)
                .subscribe()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun handleResults(updateCoin: CoinBaseUpdate) {
        if (ratesUpdateList.size >= MAX_CARD_DISPLAYED) {
            ratesUpdateList.removeAt(POP_UP_INDEX_ON_LIST_MAX_SIZE)
            ratesUpdateList.add(updateCoin)
        } else {
            ratesUpdateList.add(updateCoin)
        }
        view.notifyListUpdated()
    }

    private fun handleError(error: Throwable) {
        if(callbackAttempts > MIN_CALLBACK_ATTEMPTS_ACCEPTED) onFailureRepeatSetupService()
        disposeServices()
        Log.e(ERROR_TAG, error.message.toString())
    }

    private fun onFailureRepeatSetupService() {
        callbackAttempts--
        viewModelScope.launch {
            view.displayErrorMessage()
            repeatRequest()
        }
    }

    private fun disposeServices() {
        continuousRequest?.dispose()
        updateRequest?.dispose()
    }

    override fun onCleared() {
        disposeServices()
        super.onCleared()
    }

    interface View {
        fun notifyListUpdated()
        fun displayErrorMessage()
    }
}