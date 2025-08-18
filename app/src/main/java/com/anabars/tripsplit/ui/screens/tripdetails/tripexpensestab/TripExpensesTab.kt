package com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.model.GroupedResult
import com.anabars.tripsplit.ui.widgets.TripItemTabContent
import com.anabars.tripsplit.viewmodels.DeleteItemIntent
import com.anabars.tripsplit.viewmodels.TripItemViewModel

@Composable
fun TripExpensesTab(modifier: Modifier = Modifier) {
    val viewModel: TripItemViewModel = hiltViewModel()
    val groupedExpensesResult by viewModel.groupedExpensesResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    TripItemTabContent(
        result = groupedExpensesResult,
        placeholderTextRes = R.string.placeholder_expenses,
        successContent = { data ->
            TripExpensesContent(
                result = GroupedResult.Success(data),
                tripParticipants = tripParticipants,
                onDeleteClick = { viewModel.onIntent(DeleteItemIntent.DeleteExpenseItem(it)) }
            )
        },
        modifier = modifier
    )
}