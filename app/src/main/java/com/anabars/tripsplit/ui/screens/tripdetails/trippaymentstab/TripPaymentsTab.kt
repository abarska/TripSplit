package com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.widgets.TripItemTabContent
import com.anabars.tripsplit.viewmodels.DeleteItemIntent
import com.anabars.tripsplit.viewmodels.GroupedResult
import com.anabars.tripsplit.viewmodels.TripItemViewModel

@Composable
fun TripPaymentsTab(modifier: Modifier = Modifier) {
    val viewModel: TripItemViewModel = hiltViewModel()
    val groupedPaymentsResult by viewModel.groupedPaymentsResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    TripItemTabContent(
        result = groupedPaymentsResult,
        placeholderTextRes = R.string.placeholder_payments,
        successContent = { data ->
            TripPaymentsContent(
                result = GroupedResult.Success(data),
                tripParticipants = tripParticipants,
                onDeleteClick = { viewModel.onIntent(DeleteItemIntent.DeletePaymentItem(it)) }
            )
        },
        modifier = modifier
    )
}