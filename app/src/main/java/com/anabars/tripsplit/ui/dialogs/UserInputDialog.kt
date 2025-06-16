package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.R

@Composable
fun TsUserInputDialog(
    input: String,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {},
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
    val labelValue = if (labelRes != 0) stringResource(labelRes) else label
    TsDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        titleRes = titleRes,
        title = title,
        positiveTextRes = positiveTextRes,
        positiveText = positiveText,
        negativeTextRes = negativeTextRes,
        negativeText = negativeText
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            label = { if (labelValue.isNotEmpty()) Text(text = labelValue) },
            singleLine = true
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun TsUserInputDialogPreview() {
    TsUserInputDialog(
        input = "Placeholder",
        onInputChange = {},
        titleRes = R.string.add_a_participant,
        labelRes = R.string.participant_name_hint,
        positiveTextRes = R.string.add,
        negativeTextRes = R.string.cancel
    )
}