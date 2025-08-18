package com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.model.GroupedResult
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
import com.anabars.tripsplit.ui.components.TsOutlinedButton
import com.anabars.tripsplit.ui.listitems.TsExpenseItemRow
import com.anabars.tripsplit.utils.formatters.formatDate
import java.time.LocalDate

@Composable
fun TripExpensesContent(
    result: GroupedResult<LocalDate, ExpenseWithParticipants>,
    tripParticipants: List<TripParticipant>,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val savedMap: Map<Long, Boolean> =
        rememberSaveable { mutableStateOf(emptyMap<Long, Boolean>()) }.value
    // Non-saveable derived state
    val expandedMap = remember { mutableStateMapOf<Long, Boolean>() }
    LaunchedEffect(Unit) {
        expandedMap.putAll(savedMap)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val groupedExpenses = (result as GroupedResult.Success).data
        groupedExpenses.forEach { (date, group) ->
            item {
                TsOutlinedButton(
                    text = formatDate(date),
                    modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
                ) {}
            }
            items(items = group) { expenseWithParticipants ->
                val expenseId = expenseWithParticipants.expense.id
                val isExpanded = expandedMap[expenseId] ?: false
                TsExpenseItemRow(
                    expense = expenseWithParticipants.expense,
                    paidFor = expenseWithParticipants.participants,
                    tripParticipants = tripParticipants,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    isExpanded = isExpanded,
                    onExpandToggle = {
                        expandedMap[expenseId] = !isExpanded
                    },
                    onDeleteClick = {
                        expandedMap.remove(expenseId)
                        onDeleteClick(expenseId)
                    },
                )
            }
        }
    }
}