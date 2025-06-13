package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun ExpenseItemRow(
    expense: TripExpense,
    modifier: Modifier = Modifier,
) {
    TripSplitItemRow(
        modifier = modifier.inputWidthModifier(),
        onItemClick = {}) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = expense.category.icon,
                contentDescription = stringResource(R.string.expense_category_icon),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                InfoText(textRes = expense.category.titleRes)
                InfoText(text = "${stringResource(R.string.expense_paid_by)} ${expense.paidBy}")
            }
            Spacer(modifier = Modifier.width(16.dp))
            InfoText(
                text = "${expense.currency} ${expense.amount}",
                isHeader = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpenseItemRowPreview() {
    val data = TripExpense(
        paidBy = "Mommy",
        amount = 50.0,
        currency = "EUR",
        category = ExpenseCategory.Accommodation,
        timestamp = System.currentTimeMillis(),
        tripId = 0
    )
    ExpenseItemRow(expense = data)
}