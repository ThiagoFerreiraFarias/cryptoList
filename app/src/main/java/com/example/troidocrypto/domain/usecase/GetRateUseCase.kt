package com.example.troidocrypto.domain.usecase

import com.example.troidocrypto.domain.data.Api
import com.example.troidocrypto.domain.data.CoinBaseUpdate
import io.reactivex.Single

class GetRateUseCase {

    private val cryptoApi = Api.cryptoApi

    fun getRateUpdate(): Single<CoinBaseUpdate> = cryptoApi.getRateAsync()
}