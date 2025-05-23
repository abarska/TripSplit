package com.anabars.tripsplit.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction

@Composable
fun LabelText(modifier: Modifier = Modifier, @StringRes labelRes: Int = 0, label: String = "") {
    val value = if (labelRes != 0) stringResource(labelRes) else label
    if (value.isNotEmpty()) {
        Text(
            modifier = modifier,
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun ButtonText(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = modifier,
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ShortInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes labelRes: Int = 0,
    label: String = "",
    isError: Boolean = false,
    onValueChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        modifier = modifier.fillMaxWidth(),
        maxLines = 1,
        value = value,
        isError = isError,
        label = {
            LabelText(
                modifier = modifier,
                labelRes = labelRes,
                label = label
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        onValueChange = onValueChanged
    )
}