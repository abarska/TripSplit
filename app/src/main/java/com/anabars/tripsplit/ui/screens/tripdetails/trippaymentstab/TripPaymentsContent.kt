package com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab

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
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.ui.components.TsOutlinedButton
import com.anabars.tripsplit.ui.listitems.TsPaymentItemRow
import com.anabars.tripsplit.utils.formatters.formatDate
import com.anabars.tripsplit.viewmodels.GroupedResult

@Composable
fun TripPaymentsContent(
    modifier: Modifier = Modifier,
    result: GroupedResult<TripPayment>,
    tripParticipants: List<TripParticipant>,
    onDeleteClick: (Long) -> Unit
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
        val groupedPayments = (result as GroupedResult.Success).data
        groupedPayments.forEach { (date, group) ->
            item {
                TsOutlinedButton(
                    text = formatDate(date),
                    modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally)
                ) {}
            }
            items(items = group) { payment ->
                val isExpanded = expandedMap[payment.id] ?: false
                TsPaymentItemRow(
                    payment = payment,
                    fromParticipant = tripParticipants.firstOrNull { it.id == payment.fromUserId },
                    toParticipant = tripParticipants.firstOrNull { it.id == payment.toUserId },
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    isExpanded = isExpanded,
                    onExpandToggle = {
                        expandedMap[payment.id] = !isExpanded
                    },
                    onDeleteClick = {
                        expandedMap.remove(payment.id)
                        onDeleteClick(payment.id)
                    }
                )
            }
        }
    }
}