package com.anabars.tripsplit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anabars.tripsplit.navigation.screens.HomeScreen
import com.anabars.tripsplit.navigation.screens.AppScreens
import com.anabars.tripsplit.navigation.screens.SettingsScreen
import com.anabars.tripsplit.navigation.screens.TripsScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.ROUTE_HOME,
        modifier = modifier
    ) {
        composable(route = AppScreens.ROUTE_HOME) {
            HomeScreen(navController = navController, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_TRIPS + "/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            TripsScreen(navController = navController, itemId = itemId, modifier = modifier)
        }
        composable(route = AppScreens.ROUTE_SETTINGS) {
            SettingsScreen(navController = navController, modifier = modifier)
        }
    }
}