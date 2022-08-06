package com.example.troidocrypto.domain.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

object Api {

    private const val BASE_URL = "https://api.coindesk.com/v1/bpi/"
    private val clientInterceptor = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }).build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(clientInterceptor)
        .build()

    val cryptoApi: CryptoApi = retrofit.create(CryptoApi::class.java)
}

interface CryptoApi{

    @GET("currentprice.json")
    fun getRateAsync(): Single<CoinBaseUpdate>
}