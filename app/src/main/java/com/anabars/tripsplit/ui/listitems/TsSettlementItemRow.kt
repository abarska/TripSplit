package com.anabars.tripsplit.ui.listitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripSettlement
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeSettlement
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.ui.widgets.TsExpandCollapseToggle
import com.anabars.tripsplit.utils.formatAmount

@Composable
fun TsSettlementItemRow(
    settlement: TripSettlement,
    fromParticipant: TripParticipant,
    toParticipant: TripParticipant,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    TsItemRow(
        modifier = modifier.inputWidthModifier(),
        onItemClick = {}
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            VisiblePart(
                settlement = settlement,
                fromParticipant = fromParticipant,
                toParticipant = toParticipant,
                isExpanded = isExpanded
            ) { onExpandToggle() }
            AnimatedVisibility(visible = isExpanded) {
                TsItemRowHiddenButton(labelRes = R.string.delete, onClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun VisiblePart(
    settlement: TripSettlement,
    fromParticipant: TripParticipant,
    toParticipant: TripParticipant,
    isExpanded: Boolean,
    action: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
            contentDescription = stringResource(R.string.expense_category_icon),
            modifier = Modifier.size(36.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            TsInfoText(textRes = R.string.settlement)
            TsInfoText(text = "${stringResource(R.string.from)} ${fromParticipant.name}")
            TsInfoText(text = "${stringResource(R.string.to)} ${toParticipant.name}")
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            val formattedAmount = formatAmount(
                settlement.amount,
                stringResource(R.string.currency_format)
            )
            TsInfoText(
                text = "${settlement.currency} $formattedAmount",
                fontSize = TsFontSize.MEDIUM
            )
            Spacer(modifier = Modifier.height(16.dp))
            TsExpandCollapseToggle(isExpanded = isExpanded, action = action)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsSettlementItemRowPreviewCollapsed() {
    TsSettlementItemRow(
        settlement = getFakeSettlement(),
        fromParticipant = getFakeTripParticipants()[0],
        toParticipant = getFakeTripParticipants()[1],
        modifier = Modifier.padding(8.dp),
        isExpanded = false,
        onExpandToggle = {},
        onDeleteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TsSettlementItemRowPreviewExpanded() {
    TsSettlementItemRow(
        settlement = getFakeSettlement(),
        fromParticipant = getFakeTripParticipants()[1],
        toParticipant = getFakeTripParticipants()[0],
        modifier = Modifier.padding(8.dp),
        isExpanded = true,
        onExpandToggle = {},
        onDeleteClick = {}
    )
}