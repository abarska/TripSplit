package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeBalanceUiState
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.viewmodels.BalanceUiState

@Composable
fun TsBalanceItem(modifier: Modifier = Modifier, uiState: BalanceUiState) {
    Row(
        modifier = modifier
            .inputWidthModifier()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val amountDouble = uiState.amount.toDouble()
        TsInfoText(
            text = (uiState).participantName,
            fontSize = TsFontSize.MEDIUM
        )
        TsInfoText(
            text = "${uiState.currency} $amountDouble",
            fontSize = TsFontSize.MEDIUM,
            textColor = when {
                amountDouble > 0 -> MaterialTheme.colorScheme.primary
                amountDouble < 0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PositiveBalanceItemPreview() {
    TsBalanceItem(
        uiState = getFakeBalanceUiState(100.00)
    )
}

@Preview(showBackground = true)
@Composable
private fun NegativeBalanceItemPreview() {
    TsBalanceItem(
        uiState = getFakeBalanceUiState(-100.00)
    )
}

@Preview(showBackground = true)
@Composable
private fun NeutralBalanceItemPreview() {
    TsBalanceItem(
        uiState = getFakeBalanceUiState(0.00)
    )
}