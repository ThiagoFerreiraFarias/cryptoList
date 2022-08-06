package com.example.troidocrypto.domain.data

data class CoinBaseUpdate(
    var time: Time? = Time(),
    var chartName: String? = null,
    var bpi: BPI? = null
)