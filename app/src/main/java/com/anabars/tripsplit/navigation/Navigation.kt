package com.anabars.tripsplit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.screens.JoinExistingTripScreen
import com.anabars.tripsplit.ui.screens.NewTripScreen
import com.anabars.tripsplit.ui.screens.SettingsScreen
import com.anabars.tripsplit.ui.screens.TripsScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.ROUTE_TRIPS,
        modifier = modifier
    ) {
        composable(route = AppScreens.ROUTE_TRIPS) {
            TripsScreen(navController = navController, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_NEW_TRIP) {
            NewTripScreen(navController = navController, modifier = modifier)
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