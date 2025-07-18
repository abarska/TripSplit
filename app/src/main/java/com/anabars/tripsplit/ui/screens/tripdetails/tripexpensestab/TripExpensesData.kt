package com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.listitems.TsDateHeader
import com.anabars.tripsplit.ui.listitems.TsExpenseItemRow
import com.anabars.tripsplit.utils.formatters.formatDate
import com.anabars.tripsplit.viewmodels.GroupedExpensesResult
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TripExpensesData(
    groupedExpensesResult: GroupedExpensesResult,
    tripParticipants: List<TripParticipant>,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val groupedExpenses =
            (groupedExpensesResult as GroupedExpensesResult.Success).data
        groupedExpenses.forEach { (date, group) ->
            item {
                TsDateHeader(formattedDate = formatDate(date))
            }
            items(group) { expenseWithParticipants ->
                TsExpenseItemRow(
                    expense = expenseWithParticipants.expense,
                    paidFor = expenseWithParticipants.participants,
                    tripParticipants = tripParticipants,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    onDeleteClick = { onDeleteClick(expenseWithParticipants.expense.id) }
                )
            }
        }
    }
}