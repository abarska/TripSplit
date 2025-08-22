package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.getFakePayment
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.widgets.TsBaseItemRow
import com.anabars.tripsplit.ui.widgets.TsBaseVisiblePart
import com.anabars.tripsplit.utils.formatters.formatAsCurrency

@Composable
fun TsPaymentItemRow(
    payment: TripPayment,
    fromParticipant: TripParticipant?,
    toParticipant: TripParticipant?,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    TsBaseItemRow(
        modifier = modifier,
        isExpanded = isExpanded,
        onDeleteClick = onDeleteClick
    ) {
        val pattern = stringResource(R.string.currency_formatting_pattern)
        val amountText = "${payment.currency} ${payment.amount.formatAsCurrency(pattern)}"
        TsBaseVisiblePart(
            icon = Icons.AutoMirrored.Outlined.CompareArrows,
            iconContentDescription = stringResource(R.string.payment),
            isExpanded = isExpanded,
            amountText = amountText,
            action = onExpandToggle
        ) {
            TsInfoText(textRes = R.string.payment)
            fromParticipant?.let {
                TsInfoText(text = "${stringResource(R.string.from)} ${it.name}")
            }
            toParticipant?.let {
                TsInfoText(text = "${stringResource(R.string.to)} ${it.name}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsPaymentItemRowPreviewCollapsed() {
    TsPaymentItemRow(
        payment = getFakePayment(),
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
private fun TsPaymentItemRowPreviewExpanded() {
    TsPaymentItemRow(
        payment = getFakePayment(),
        fromParticipant = getFakeTripParticipants()[1],
        toParticipant = getFakeTripParticipants()[0],
        modifier = Modifier.padding(8.dp),
        isExpanded = true,
        onExpandToggle = {},
        onDeleteClick = {}
    )
}