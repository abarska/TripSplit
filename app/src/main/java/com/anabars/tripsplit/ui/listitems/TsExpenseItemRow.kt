package com.anabars.tripsplit.ui.listitems

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripExpense
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.utils.formatAmount

@Composable
fun TsExpenseItemRow(
    expense: TripExpense,
    paidFor: List<TripParticipant>,
    tripParticipants: List<TripParticipant>,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    TsItemRow(
        modifier = modifier.inputWidthModifier(),
        onItemClick = {}
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            VisiblePart(
                expense = expense,
                paidFor = paidFor,
                tripParticipants = tripParticipants,
                isExpanded = isExpanded
            ) { isExpanded = !isExpanded }
            AnimatedVisibility(visible = isExpanded) {
                HiddenPart(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
private fun VisiblePart(
    expense: TripExpense,
    paidFor: List<TripParticipant>,
    tripParticipants: List<TripParticipant>,
    isExpanded: Boolean,
    action: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        Icon(
            imageVector = expense.category.icon,
            contentDescription = stringResource(R.string.expense_category_icon),
            modifier = Modifier.size(36.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            TsInfoText(textRes = expense.category.titleRes)
            val payer = tripParticipants.find { it.id == expense.paidById }
            val paidForText = if (paidFor.size == tripParticipants.size) {
                LocalContext.current.getString(R.string.everyone)
            } else {
                paidFor.joinToString(", ") { it.name }
            }
            payer?.let {
                TsInfoText(text = "${stringResource(R.string.expense_paid_by)} ${it.name}")
                TsInfoText(text = "${stringResource(R.string.expense_paid_for)} $paidForText")
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            val formattedAmount = formatAmount(
                expense.amount,
                stringResource(R.string.currency_format)
            )
            TsInfoText(
                text = "${expense.currency} $formattedAmount",
                fontSize = TsFontSize.MEDIUM
            )
            Spacer(modifier = Modifier.height(16.dp))
            val contentDescriptionRes =
                if (isExpanded) R.string.collapse_card_icon
                else R.string.expand_card_icon
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = stringResource(contentDescriptionRes),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { action() }
            )
        }
    }
}

@Composable
private fun HiddenPart(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        TextButton(onClick = onEditClick) {
            TsInfoText(
                text = stringResource(R.string.edit),
                fontSize = TsFontSize.MEDIUM,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
        TextButton(onClick = onDeleteClick) {
            TsInfoText(
                text = stringResource(R.string.delete),
                fontSize = TsFontSize.MEDIUM,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsExpenseItemRowPreview() {
    TsExpenseItemRow(
        expense = getFakeTripExpense(),
        paidFor = getFakeTripParticipants(),
        tripParticipants = getFakeTripParticipants()
    )
}