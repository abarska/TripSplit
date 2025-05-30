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
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.ui.components.TripSplitFab
import com.anabars.tripsplit.ui.itemrows.TripItemRow
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.utils.fullScreenModifier
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun TripsScreen(
    navController: NavController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier
) {
    val trips by tripViewModel.tripList.collectAsState()

    Box(
        modifier = modifier.then(Modifier.fullScreenModifier())
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (trips.isNotEmpty()) {
                items(items = trips) { trip ->
                    TripItemRow(text = tripText(trip))
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

private fun tripText(trip: Trip): String =
    if (trip.description.isEmpty()) trip.title
    else String.format("%s (%s)", trip.title, trip.description)
