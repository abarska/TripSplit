package com.anabars.tripsplit.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.settings.SettingsScreen
import com.anabars.tripsplit.ui.screens.tripdetails.TripDetailsScreen
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.SharedViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
) {
    val onTabTitleChange = { tabTitle: String ->
        sharedViewModel.setTabTitle(tabTitle)
    }
    NavHost(
        navController = navController,
        startDestination = AppScreens.ROUTE_TRIPS,
        modifier = modifier.fillMaxSize()
    ) {
        composable(route = AppScreens.ROUTE_TRIPS) {
            TripsScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                modifier = modifier
            )
        }
        composable(route = AppScreens.ROUTE_ADD_TRIP) {
            AddTripScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                sharedViewModel = sharedViewModel
            )
        }
        composable(
            route = AppScreens.ROUTE_ADD_EXPENSE + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getLong("tripId") ?: return@composable
            AddExpenseScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                sharedViewModel = sharedViewModel
            )
        }
        composable(route = AppScreens.ROUTE_SETTINGS) {
            SettingsScreen(
                onTabTitleChange = onTabTitleChange,
                modifier = modifier
            )
        }
        composable(
            route = AppScreens.ROUTE_TRIP_DETAILS + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getLong("id") ?: return@composable
            TripDetailsScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
            )
        }
    }
}