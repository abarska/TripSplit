package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsFab
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.viewmodels.TripPaymentViewModel

@Composable
fun TripPaymentTab(
    navController: NavController,
    onTabTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TripPaymentViewModel = hiltViewModel()
    val screenTitle = String.format(
        "%s: %s",
        stringResource(R.string.title_trip_details),
        stringResource(R.string.title_tab_payments)
    )
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    Box(modifier = modifier.fillMaxSize()) {
        TsFab(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_payment,
        ) {
            navController.navigate("${AppScreens.ROUTE_ADD_PAYMENT}/${viewModel.tripId}")
        }
    }
}