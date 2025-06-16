package com.anabars.tripsplit.ui.components


import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun DateInputSection(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePattern = DateTimeFormatter.ofPattern(stringResource(R.string.datepicker_format))
    val formattedDate = rememberSaveable(selectedDate) { selectedDate.format(datePattern) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val onDateSetListener = { _: DatePicker, y: Int, m: Int, d: Int ->
        val newDate = LocalDate.of(y, m + 1, d)
        onDateSelected(newDate)
        showDatePicker = false
    }

    OutlinedButton(
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        onClick = { showDatePicker = true }
    ) {
        Text(text = formattedDate)
    }

    if (showDatePicker) {
        val year = selectedDate.year
        val month = selectedDate.monthValue - 1
        val day = selectedDate.dayOfMonth
        DatePickerDialog(context, onDateSetListener, year, month, day).apply {
            datePicker.maxDate = calendar.timeInMillis
            setOnCancelListener { showDatePicker = false }
        }.show()
    }
}