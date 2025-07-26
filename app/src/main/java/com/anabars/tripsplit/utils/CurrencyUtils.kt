package com.anabars.tripsplit.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.anabars.tripsplit.TripSplitApplication
import java.util.Currency
import java.util.Locale

fun getDefaultCurrency(): String {
    val context = TripSplitApplication.instance.applicationContext
    val simCurrency = getCurrencyFromSim(context)
    val localeCurrency = getCurrencyFromLocale()
    return simCurrency ?: localeCurrency ?: "UAH"
}

private fun getCurrencyFromSim(context: Context): String? {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val country = tm.simCountryIso.uppercase(Locale.US)
    return try {
        Currency.getInstance(Locale("", country)).currencyCode
    } catch (e: Exception) {
        null
    }
}

private fun getCurrencyFromLocale(): String? {
    return try {
        Currency.getInstance(Locale.getDefault()).currencyCode
    } catch (e: IllegalArgumentException) {
        null
    }
}

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

val validCurrencyCodesCached: Set<String> by lazy {
    Locale.getAvailableLocales()
        .asSequence()
        .filter { it.country.isNotEmpty() }
        .mapNotNull {
            try {
                Currency.getInstance(it).currencyCode
            } catch (_: IllegalArgumentException) {
                null
            }
        }
        .toSet()
}