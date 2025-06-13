package com.anabars.tripsplit.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun ShortInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes labelRes: Int = 0,
    label: String = "",
    isError: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
    prefix: @Composable (() -> Unit)? = null,
    onValueChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        modifier = modifier,
        maxLines = 1,
        value = value,
        isError = isError,
        label = {
            LabelText(
                modifier = modifier,
                textRes = labelRes,
                text = label,
                isError = isError
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        textStyle = textStyle,
        prefix = prefix,
        onValueChange = onValueChanged
    )
}

@Preview(showBackground = true)
@Composable
private fun ShortInputPreview() {
    ShortInputTextField(
        modifier = Modifier.inputWidthModifier(),
        labelRes = R.string.app_name,
        value = "placeholder"
    ) {}
}