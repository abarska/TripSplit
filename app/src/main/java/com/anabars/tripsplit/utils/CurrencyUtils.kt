package com.anabars.tripsplit.utils

import java.util.Currency
import java.util.Locale

fun getCurrencyDisplayList(currencyCodes: Set<String>): List<String> {
    return currencyCodes.mapNotNull { code ->
        try {
            val currency = Currency.getInstance(code)
            val displayName = currency.displayName
            "$code - $displayName"
        } catch (e: IllegalArgumentException) {
            null
        }
    }.sorted()
}

fun validCurrencyCodes() = Locale.getAvailableLocales()
    .asSequence()
    .filter { it.country.isNotEmpty() }
    .mapNotNull { locale ->
        try {
            Currency.getInstance(locale).currencyCode
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    .toSet()