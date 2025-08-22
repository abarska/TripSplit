package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeBalanceWithNameAndStatus
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.utils.formatters.formatAsCurrency

@Composable
fun TsBalanceItem(modifier: Modifier = Modifier, balance: BalanceWithNameAndStatus) {
    Row(
        modifier = modifier
            .inputWidthModifier()
            .padding(horizontal = 16.dp, vertical = dimensionResource(R.dimen.padding_small)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val amount = balance.amount.toDouble()
        val amountTextColor = when {
            amount > 0 -> MaterialTheme.colorScheme.primary
            amount < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        val formattedAmount = balance.amount.formatAsCurrency()
        TsInfoText(
            text = balance.participantName,
            fontSize = TsFontSize.MEDIUM
        )
        TsInfoText(
            text = "${balance.amountCurrency} $formattedAmount",
            fontSize = TsFontSize.MEDIUM,
            textColor = amountTextColor
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