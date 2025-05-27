package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    TripSplitDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        titleRes = titleRes,
        title = title,
        positiveTextRes = positiveTextRes,
        positiveText = positiveText,
        negativeTextRes = negativeTextRes,
        negativeText = negativeText,
    ) {
        InfoText(
            textRes = questionRes,
            text = question
        )
    }
}