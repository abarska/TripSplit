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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.getFakeTripExpense
import com.anabars.tripsplit.ui.utils.getFakeTripParticipants
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import java.text.DecimalFormat

@Composable
fun TsExpenseItemRow(
    expense: TripExpense,
    participants: List<TripParticipant>,
    modifier: Modifier = Modifier,
) {
    TsItemRow(
        modifier = modifier.inputWidthModifier(),
        onItemClick = {}
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            Icon(
                imageVector = expense.category.icon,
                contentDescription = stringResource(R.string.expense_category_icon),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                TsInfoText(textRes = expense.category.titleRes)
                val payer = participants.find { it.id == expense.paidById }
                payer?.let {
                    TsInfoText(text = "${stringResource(R.string.expense_paid_by)} ${it.name}")
                    TsInfoText(text = "${stringResource(R.string.expense_paid_for)} ${context.getString(R.string.everyone)}")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            val formatter = DecimalFormat(context.getString(R.string.currency_format))
            TsInfoText(
                text = "${expense.currency} ${formatter.format(expense.amount)}",
                isHeader = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsExpenseItemRowPreview() {
    TsExpenseItemRow(expense = getFakeTripExpense(), participants = getFakeTripParticipants())
}