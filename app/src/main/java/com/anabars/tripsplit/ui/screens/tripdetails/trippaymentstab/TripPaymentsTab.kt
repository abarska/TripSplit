package com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsFab
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.widgets.TsPlaceholderView
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import com.anabars.tripsplit.viewmodels.GroupedPaymentsResult
import com.anabars.tripsplit.viewmodels.TripItemViewModel

@Composable
fun TripPaymentsTab(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: TripItemViewModel = hiltViewModel()
    val groupedPaymentsResult by viewModel.groupedPaymentsResult.collectAsState()
    val tripParticipants by viewModel.tripParticipants.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (groupedPaymentsResult) {
            is GroupedPaymentsResult.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is GroupedPaymentsResult.Empty ->
                TsPlaceholderView(
                    painterRes = R.drawable.empty_wallet_image,
                    contentDescriptionRes = R.string.empty_wallet_image,
                    textRes = R.string.placeholder_payments
                )

            is GroupedPaymentsResult.Success ->
                TripPaymentsData(
                    groupedPaymentsResult = groupedPaymentsResult,
                    tripParticipants = tripParticipants,
                    onDeleteClick = { paymentId -> viewModel.deletePaymentById(paymentId) }
                )
        }

        TsFab(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_payment,
        ) {
            navController.navigate(
                "${AppScreens.ROUTE_ADD_PAYMENT}/${viewModel.tripId}/${AddItemViewModel.UseCase.PAYMENT.name}"
            )
        }
    }
}