package com.anabars.tripsplit.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun formatDate(time: Long) = formatDate(Date(time))

@Composable
fun formatDate(date: Date): String {
    val format = SimpleDateFormat(stringResource(R.string.time_format), Locale.getDefault())
    return format.format(date)
}