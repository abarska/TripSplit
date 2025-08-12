package com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.widgets.TripItemTabContent
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import com.anabars.tripsplit.viewmodels.GroupedResult
import com.anabars.tripsplit.viewmodels.TripItemViewModel

@Composable
fun TripPaymentsTab(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: TripItemViewModel = hiltViewModel()
    val groupedPaymentsResult by viewModel.groupedPaymentsResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    TripItemTabContent(
        result = groupedPaymentsResult,
        placeholderTextRes = R.string.placeholder_payments,
        fabDescriptionRes = R.string.add_a_new_payment,
        fabClickRoute = "${AppScreens.ROUTE_ADD_PAYMENT}/${viewModel.tripId}/${AddItemViewModel.UseCase.PAYMENT.name}",
        successContent = { data ->
            TripPaymentsContent(
                result = GroupedResult.Success(data),
                tripParticipants = tripParticipants,
                onDeleteClick = { viewModel.deletePaymentById(it) }
            )
        },
        navController = navController,
        modifier = modifier
    )
}