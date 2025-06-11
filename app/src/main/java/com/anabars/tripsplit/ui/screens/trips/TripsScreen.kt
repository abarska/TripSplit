package com.anabars.tripsplit.ui.screens.trips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.TripSplitFab
import com.anabars.tripsplit.ui.itemrows.TripSplitItemRow
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.viewmodels.TripsViewModel

@Composable
fun TripsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val tripsViewModel: TripsViewModel = hiltViewModel()
    val trips by tripsViewModel.tripList.collectAsState()

    Box(
        modifier = modifier.padding(dimensionResource(R.dimen.full_screen_padding))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (trips.isNotEmpty()) {
                items(items = trips) { trip ->
                    TripSplitItemRow(
                        modifier = modifier.inputWidthModifier(),
                        onItemClick = { navController.navigate(AppScreens.ROUTE_TRIP_DETAILS + "/${trip.id}") }
                    ) {
                        InfoText(modifier = Modifier.padding(8.dp), text = trip.title)
                    }
                    if (trip != trips.last()) {
                        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
                    }
                }
            }
        }
        TripSplitFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            iconVector = Icons.Outlined.Add,
            contentDescription = R.string.add_a_new_trip,
        ) {
            navController.navigate(AppScreens.ROUTE_ADD_TRIP) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
