package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun TsDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    @StringRes titleRes: Int = 0,
    title: String = "",
    @StringRes positiveTextRes: Int = 0,
    positiveText: String = "",
    @StringRes negativeTextRes: Int = 0,
    negativeText: String = "",
    content: @Composable () -> Unit = {}
) {
    val titleValue =
        if (titleRes != 0) stringResource(titleRes) else title
    val positiveTextValue =
        if (positiveTextRes != 0) stringResource(positiveTextRes) else positiveText
    val negativeTextValue =
        if (negativeTextRes != 0) stringResource(negativeTextRes) else negativeText

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { if (titleValue.isNotEmpty()) TsInfoText(text = titleValue, fontSize = TsFontSize.LARGE) },
        text = { content() },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            if (positiveTextValue.isNotEmpty()) {
                TextButton(onClick = onConfirm) {
                    TsInfoText(text = positiveTextValue, fontSize = TsFontSize.MEDIUM, textColor = MaterialTheme.colorScheme.primary)
                }
            }
        },
        dismissButton = {
            if (negativeTextValue.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    TsInfoText(text = negativeTextValue, fontSize = TsFontSize.MEDIUM, textColor = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
}