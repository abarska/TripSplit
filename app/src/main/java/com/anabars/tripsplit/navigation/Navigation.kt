package com.anabars.tripsplit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.screens.JoinExistingTripScreen
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.SettingsScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.TripViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    tripViewModel: TripViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.ROUTE_TRIPS,
        modifier = modifier
    ) {
        composable(route = AppScreens.ROUTE_TRIPS) {
            TripsScreen(navController = navController, tripViewModel = tripViewModel, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_ADD_TRIP) {
            AddTripScreen(navController = navController, tripViewModel = tripViewModel, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_EXISTING_TRIP) {
            JoinExistingTripScreen(navController = navController, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_SETTINGS) {
            SettingsScreen(navController = navController, modifier = modifier)
        }
    }
}

//composable(route = AppScreens.ROUTE_TRIPS + "/{itemId}") { backStackEntry ->
//    val itemId = backStackEntry.arguments?.getString("itemId")
//    TripsScreen(navController = navController, itemId = itemId, modifier = modifier)
//}