package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.ui.utils.getFakeAddTripUiState

@Composable
fun TsUserInputDialog(
    uiState: AddTripUiState,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onMultiplicatorChange: (Int) -> Unit = {},
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.newParticipantName,
                onValueChange = onInputChange,
                label = { if (labelValue.isNotEmpty()) Text(text = labelValue) },
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.vertical_spacer_normal),
                    Alignment.CenterHorizontally
                )
            ) {
                TsItemRow(
                    highlighted = uiState.newParticipantMultiplicator > 1,
                    enabled = uiState.newParticipantMultiplicator > 1,
                    onItemClick = { onMultiplicatorChange(uiState.newParticipantMultiplicator - 1) }
                ) {
                    Row(
                        modifier = Modifier
                            .width(48.dp)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TsInfoText(text = stringResource(R.string.minus_one))
                    }
                }
                Text(
                    text = stringResource(R.string.pays_for_format, uiState.newParticipantMultiplicator),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
                TsItemRow(
                    highlighted = true,
                    onItemClick = { onMultiplicatorChange(uiState.newParticipantMultiplicator + 1) }
                ) {
                    Row(
                        modifier = Modifier
                            .width(48.dp)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TsInfoText(text = stringResource(R.string.plus_one))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsUserInputDialogPreview() {
    TsUserInputDialog(
        uiState = getFakeAddTripUiState(),
        onInputChange = {},
        titleRes = R.string.add_participant,
        labelRes = R.string.participant_name_hint,
        positiveTextRes = R.string.add,
        negativeTextRes = R.string.cancel
    )
}