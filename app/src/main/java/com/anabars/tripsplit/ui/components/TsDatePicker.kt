package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.utils.TsFontSize
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputSection(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePattern = DateTimeFormatter.ofPattern(stringResource(R.string.date_picker_format))
    val formattedDate = rememberSaveable(selectedDate) { selectedDate.format(datePattern) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    TsOutlinedButton(
        text = formattedDate,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
    ) { showDatePicker = true }

    if (showDatePicker) {
        val initialDate = remember(selectedDate) {
            selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val maxSelectableDate = remember { LocalDate.now(ZoneOffset.UTC) }
        val maxSelectableYear = remember { maxSelectableDate.year }
        val tomorrowStartOfDayMillis = remember {
            maxSelectableDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val selectableDates = remember(
            maxSelectableDate,
            maxSelectableYear,
            tomorrowStartOfDayMillis
        ) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis < tomorrowStartOfDayMillis
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= maxSelectableYear
                }
            }
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate,
            selectableDates = selectableDates
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate =
                                Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            onDateSelected(newDate)
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    TsInfoText(
                        textRes = android.R.string.ok,
                        fontSize = TsFontSize.MEDIUM,
                        textColor = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    TsInfoText(
                        textRes = R.string.cancel,
                        fontSize = TsFontSize.MEDIUM,
                        textColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}