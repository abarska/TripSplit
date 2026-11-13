package com.anabars.tripsplit.utils.formatters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun formatDate(time: Long) = formatDate(Date(time))

@Composable
fun formatDate(date: Date): String {
    val formatPattern = stringResource(R.string.time_format)
    val locale = currentLocale()
    val formatter = remember(formatPattern, locale) { SimpleDateFormat(formatPattern, locale) }
    return formatter.format(date)
}

@Composable
fun formatDate(localDate: LocalDate): String {
    val formatPattern = stringResource(R.string.date_picker_format)
    val locale = currentLocale()
    val formatter = remember(formatPattern, locale) { DateTimeFormatter.ofPattern(formatPattern, locale) }
    return localDate.format(formatter)
}

@Composable
private fun currentLocale(): Locale = LocalConfiguration.current.locales[0]
