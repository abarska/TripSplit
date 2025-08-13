package com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.navigation.Routes
import com.anabars.tripsplit.ui.widgets.TripItemTabContent
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import com.anabars.tripsplit.viewmodels.DeleteItemIntent
import com.anabars.tripsplit.viewmodels.GroupedResult
import com.anabars.tripsplit.viewmodels.TripItemViewModel

@Composable
fun TripExpensesTab(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: TripItemViewModel = hiltViewModel()
    val groupedExpensesResult by viewModel.groupedExpensesResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    TripItemTabContent(
        result = groupedExpensesResult,
        placeholderTextRes = R.string.placeholder_expenses,
        fabClickRoute = "${Routes.ROUTE_ADD_EXPENSE}/${viewModel.tripId}/${AddItemViewModel.UseCase.EXPENSE.name}",
        successContent = { data ->
            TripExpensesContent(
                result = GroupedResult.Success(data),
                tripParticipants = tripParticipants,
                onDeleteClick = { viewModel.onIntent(DeleteItemIntent.DeleteExpenseItem(it)) }
            )
        },
        navController = navController,
        modifier = modifier
    )
}