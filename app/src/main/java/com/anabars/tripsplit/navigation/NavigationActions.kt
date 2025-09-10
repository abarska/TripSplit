package com.anabars.tripsplit.navigation

import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.viewmodels.AddItemViewModel

fun onFabClicked(
    navController: NavHostController,
    selectedTabItem: TabItem
) {
    val backStackEntry = navController.currentBackStackEntry
    val tripId = backStackEntry?.arguments?.getLong("tripId")

    val currentRoute = backStackEntry?.destination?.route
        ?: navController.graph.startDestinationRoute

    val destinationRoute = when {
        currentRoute?.startsWith(AppScreens.TRIPS.route) == true -> AppScreens.ADD_TRIP.route

        currentRoute?.startsWith(AppScreens.TRIP_DETAILS.route) == true && tripId != null -> {
            when (selectedTabItem) {
                TabItem.Expenses ->
                    "${AppScreens.ADD_EXPENSE.route}/$tripId/${AddItemViewModel.UseCase.EXPENSE.name}"

                TabItem.Payments ->
                    "${AppScreens.ADD_PAYMENT.route}/$tripId/${AddItemViewModel.UseCase.PAYMENT.name}"

                else -> null
            }
        }

        else -> null
    }
    destinationRoute?.let { route -> navController.navigate(route) }
}

