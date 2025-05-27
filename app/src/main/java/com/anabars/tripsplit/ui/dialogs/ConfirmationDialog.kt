package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.ui.components.InfoText

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    @StringRes titleRes: Int = 0,
    title: String = "",
    @StringRes questionRes: Int = 0,
    question: String = "",
    @StringRes positiveTextRes: Int = 0,
    positiveText: String = "",
    @StringRes negativeTextRes: Int = 0,
    negativeText: String = ""
) {
    val titleValue =
        if (titleRes != 0) stringResource(titleRes) else title
    val positiveTextValue =
        if (positiveTextRes != 0) stringResource(positiveTextRes) else positiveText
    val negativeTextValue =
        if (negativeTextRes != 0) stringResource(negativeTextRes) else negativeText

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titleValue) },
        text = {
            InfoText(
                modifier = modifier,
                textRes = questionRes,
                text = question
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = positiveTextValue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = negativeTextValue)
            }
        }
    )
}