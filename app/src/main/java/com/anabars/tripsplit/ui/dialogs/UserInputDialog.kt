package com.anabars.tripsplit.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsShortInput
import com.anabars.tripsplit.ui.model.AddTripUiState
import com.anabars.tripsplit.ui.model.UserInputActions
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeAddTripUiState

@Composable
fun TsUserInputDialog(
    uiState: AddTripUiState,
    actions: UserInputActions,
    modifier: Modifier = Modifier,
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
        onDismiss = actions.onDismiss,
        onConfirm = actions.onConfirm,
        titleRes = titleRes,
        title = title,
        positiveTextRes = positiveTextRes,
        positiveText = positiveText,
        negativeTextRes = negativeTextRes,
        negativeText = negativeText
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_normal))
        ) {
            TsShortInput(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.newParticipantName,
                onValueChanged = actions.onInputChange,
                label = labelValue
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_normal),
                    Alignment.CenterHorizontally
                )
            ) {
                TsContentCard (
                    isHighlighted = uiState.newParticipantMultiplicator > 1,
                    isEnabled = uiState.newParticipantMultiplicator > 1,
                    onItemClick = { actions.onMultiplicatorChange(uiState.newParticipantMultiplicator - 1) }
                ) {
                    Row(
                        modifier = Modifier
                            .width(48.dp)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TsInfoText(
                            text = stringResource(R.string.minus_one),
                            fontSize = TsFontSize.MEDIUM
                        )
                    }
                }
                TsInfoText(
                    text = stringResource(
                        R.string.pays_for_format,
                        uiState.newParticipantMultiplicator
                    ),
                    fontSize = TsFontSize.MEDIUM
                )
                TsContentCard(
                    isHighlighted = true,
                    onItemClick = { actions.onMultiplicatorChange(uiState.newParticipantMultiplicator + 1) }
                ) {
                    Row(
                        modifier = Modifier
                            .width(48.dp)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TsInfoText(
                            text = stringResource(R.string.plus_one),
                            fontSize = TsFontSize.MEDIUM
                        )
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
        actions = UserInputActions(),
        titleRes = R.string.add_participant,
        labelRes = R.string.participant_name_hint,
        positiveTextRes = R.string.add,
        negativeTextRes = R.string.cancel
    )
}