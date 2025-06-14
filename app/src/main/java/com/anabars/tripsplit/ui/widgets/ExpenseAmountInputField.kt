package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.LabelText
import com.anabars.tripsplit.ui.components.ShortInputTextField

@Composable
fun ExpenseAmountInputField(
    value: String,
    currencyPrefix: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {

    // prevent negative numbers or multiple dots, allow 1 dot and 2 digits after dot
    val regex = remember { Regex("^\\d*(\\.\\d{0,2})?\$") }

    ShortInputTextField(
        value = value,
        labelRes = R.string.enter_amount_hint,
        onValueChanged = { newValue ->
            if (newValue.isEmpty() || regex.matches(newValue)) {
                onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        prefix = { LabelText(text = currencyPrefix) },
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
    )
}