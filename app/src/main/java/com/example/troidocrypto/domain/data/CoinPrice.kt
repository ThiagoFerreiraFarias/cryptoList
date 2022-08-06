package com.example.troidocrypto.domain.data

data class CoinPrice(
    var code: String? = null,
    var symbol: String? = null,
    var rate: String? = null,
    var description: String? = null,
    var rate_float: Float? = null
)