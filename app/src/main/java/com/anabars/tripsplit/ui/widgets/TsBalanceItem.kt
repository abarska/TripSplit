package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
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
            .padding(horizontal = 16.dp, vertical = dimensionResource(R.dimen.padding_small)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val signum = balance.amount.signum()
        val amountTextColor = when {
            signum > 0 -> MaterialTheme.colorScheme.primary
            signum < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        Column {
            TsInfoText(
                text = balance.participantName,
                fontSize = TsFontSize.MEDIUM
            )
            if (balance.participantStatus != ParticipantStatus.ACTIVE) {
                TsInfoText(
                    text = stringResource(R.string.balance_participant_removed),
                    fontSize = TsFontSize.SMALL
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            TsInfoText(
                text = "${balance.amountCurrency} ${balance.amount.toPlainString()}",
                fontSize = TsFontSize.MEDIUM,
                textColor = amountTextColor
            )
            if (balance.approxAmount != null && balance.approxCurrency != null) {
                TsInfoText(
                    text = stringResource(
                        R.string.balance_approx_value,
                        balance.approxCurrency,
                        balance.approxAmount.toPlainString()
                    ),
                    fontSize = TsFontSize.SMALL,
                    textColor = amountTextColor
                )
            }
        }
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

@Preview(showBackground = true)
@Composable
private fun ApproxAndRemovedBalanceItemPreview() {
    TsBalanceItem(
        balance = getFakeBalanceWithNameAndStatus(10).copy(
            participantStatus = ParticipantStatus.INACTIVE,
            approxAmount = BigDecimal("9.14"),
            approxCurrency = "GBP"
        )
    )
}
