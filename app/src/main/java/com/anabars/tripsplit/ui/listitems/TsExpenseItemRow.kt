package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.getFakeTripExpense
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.widgets.TsBaseItemRow
import com.anabars.tripsplit.ui.widgets.TsBaseVisiblePart
import com.anabars.tripsplit.utils.formatters.formatAsCurrency

@Composable
fun TsExpenseItemRow(
    expense: TripExpense,
    paidFor: List<TripParticipant>,
    tripParticipants: List<TripParticipant>,
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
        val amountText = "${expense.currency} ${expense.amount.formatAsCurrency(pattern)}"
        TsBaseVisiblePart(
            icon = expense.category.icon,
            iconContentDescription = stringResource(R.string.expense_category_icon),
            isExpanded = isExpanded,
            amountText = amountText,
            action = onExpandToggle
        ) {
            TsInfoText(textRes = expense.category.titleRes)
            val payer = tripParticipants.find { it.id == expense.paidById }
            val paidForText = paidFor.joinToString(", ") { it.name }
            payer?.let {
                TsInfoText(text = "${stringResource(R.string.expense_paid_by)} ${it.name}")
                TsInfoText(text = "${stringResource(R.string.expense_paid_for)} $paidForText")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsExpenseItemRowPreviewCollapsed() {
    TsExpenseItemRow(
        expense = getFakeTripExpense(),
        paidFor = getFakeTripParticipants(),
        tripParticipants = getFakeTripParticipants(),
        onExpandToggle = {},
        modifier = Modifier.padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun TsExpenseItemRowPreviewExpanded() {
    TsExpenseItemRow(
        expense = getFakeTripExpense(),
        paidFor = getFakeTripParticipants(),
        tripParticipants = getFakeTripParticipants(),
        isExpanded = true,
        onExpandToggle = {},
        modifier = Modifier.padding(8.dp)
    )
}