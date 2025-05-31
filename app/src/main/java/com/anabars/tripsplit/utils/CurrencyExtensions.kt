package com.anabars.tripsplit.utils

import java.util.Currency
import java.util.Locale

// this will convert the currency code to "<flag icon> <code> - <name>" format
// if flag is not available for given currency, a white flag will be displayed
fun Currency.currencyCodeToListItemText(): String {
    val locales = Locale.getAvailableLocales().filter {
        runCatching { Currency.getInstance(it) == this }.getOrNull() == true
    }
    val countryCode = locales.firstOrNull()?.country ?: ""
    val flag = if (countryCode.length == 2) countryCodeToFlag(countryCode) else "🏳️"
    return "$flag $currencyCode - $displayName"
}

fun countryCodeToFlag(countryCode: String): String {
    return countryCode.uppercase(Locale.US)
        .map { char -> Character.toChars(0x1F1E6 - 'A'.code + char.code).concatToString() }
        .joinToString("")
}