package com.anabars.tripsplit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TripSplitFab
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun TripsScreen(
    navController: NavController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        TripSplitFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
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