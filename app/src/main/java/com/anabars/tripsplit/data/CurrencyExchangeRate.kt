package com.example.marysyatravel.data

data class CurrencyExchangeRate(
    val baseCurrency: String = "EUR",
    val targetCurrency: String = "RON",
    val rate: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)