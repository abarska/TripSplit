package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun UserInputDialog(
    input: String,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onDismiss: () -> Unit = {},
    @StringRes titleRes: Int = 0,
    title: String = "",
    @StringRes labelRes: Int = 0,
    label: String = "",
    @StringRes positiveTextRes: Int = 0,
    positiveText: String = "",
    @StringRes negativeTextRes: Int = 0,
    negativeText: String = ""
) {
    val titleValue =
        if (titleRes != 0) stringResource(titleRes) else title
    val labelValue =
        if (labelRes != 0) stringResource(labelRes) else label
    val positiveTextValue =
        if (positiveTextRes != 0) stringResource(positiveTextRes) else positiveText
    val negativeTextValue =
        if (negativeTextRes != 0) stringResource(negativeTextRes) else negativeText

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { if (titleValue.isNotEmpty()) Text(text = titleValue) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                label = { if (labelValue.isNotEmpty()) Text(text = labelValue) },
                singleLine = true
            )
        },
        confirmButton = {
            if (positiveTextValue.isNotEmpty()) {
                TextButton(onClick = onSave) {
                    Text(text = positiveTextValue)
                }
            }
        },
        dismissButton = {
            if (negativeTextValue.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text(text = negativeTextValue)
                }
            }
        }
    )
}