package com.example.troidocrypto.presentation

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.troidocrypto.domain.data.CoinBaseUpdate
import com.example.troidocrypto.domain.usecase.GetRateUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val MAX_CARD_DISPLAYED = 50
const val REQUEST_TIME_GAP = 60000L

class PresenterViewModel : ViewModel() {

    private val useCase = GetRateUseCase()
    private lateinit var view: View
    private var updateList = mutableListOf<CoinBaseUpdate>()
    val cryptoData = ObservableField<MutableList<CoinBaseUpdate>>()
    private var compositeDisposable = CompositeDisposable()
    private var requestScheduler: Scheduler = AndroidSchedulers.mainThread()

    fun setView(view: View) {
        this.view = view
    }

    init {
        cryptoData.set(updateList)
        getRate()
        repeatRequest()
    }

    private fun repeatRequest() {
        compositeDisposable.add(
            Observable.interval(REQUEST_TIME_GAP, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribeOn(requestScheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getRate() }
        )
    }

    private fun getRate() {
        compositeDisposable.add(
            useCase.getUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(::handleResults)
                .doOnError(::handleError)
                .subscribe()
        )
    }

    private fun handleResults(update: CoinBaseUpdate) {
        if (updateList.size >= MAX_CARD_DISPLAYED) {
            updateList.removeAt(0)
            updateList.add(update)
        } else {
            updateList.add(update)
        }
        view.notifyListUpdated()
    }

    private fun handleError(error: Throwable) {
        Log.e("API_EXCEPTION", error.message.toString())
    }

    override fun onCleared() {
        requestScheduler.shutdown()
        compositeDisposable.dispose()
        super.onCleared()
    }

    interface View {
        fun notifyListUpdated()
    }
}