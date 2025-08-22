package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeBalanceWithNameAndStatus
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TsBalanceItem(modifier: Modifier = Modifier, balance: BalanceWithNameAndStatus) {
    Row(
        modifier = modifier
            .inputWidthModifier()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val amountDouble = balance.amount.toDouble()
        TsInfoText(
            text = balance.participantName,
            fontSize = TsFontSize.MEDIUM
        )
        TsInfoText(
            text = "USD $amountDouble",
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
    TsBalanceItem(balance = getFakeBalanceWithNameAndStatus(10))
}

@Preview(showBackground = true)
@Composable
private fun NegativeBalanceItemPreview() {
    TsBalanceItem(balance = getFakeBalanceWithNameAndStatus(-10))
}

@Preview(showBackground = true)
@Composable
private fun NeutralBalanceItemPreview() {
    TsBalanceItem(balance = getFakeBalanceWithNameAndStatus(0))
}