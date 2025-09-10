package com.anabars.tripsplit.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.viewmodels.AddItemViewModel

fun onFabClicked(
    navController: NavHostController,
    currentTripId: Long?,
    selectedTabItem: TabItem
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
        ?: navController.graph.startDestinationRoute

    currentRoute?.let { currentRoute ->
        val destinationRoute = when {
            currentRoute.startsWith(AppScreens.TRIPS.route) -> AppScreens.ADD_TRIP.route

            currentRoute.startsWith(AppScreens.TRIP_DETAILS.route) && currentTripId != null -> {
                when (selectedTabItem) {
                    TabItem.Expenses ->
                        "${AppScreens.ADD_EXPENSE.route}/${currentTripId}/${AddItemViewModel.UseCase.EXPENSE.name}"

                    TabItem.Payments ->
                        "${AppScreens.ADD_PAYMENT.route}/${currentTripId}/${AddItemViewModel.UseCase.PAYMENT.name}"

                    else -> null
                }
            }

            else -> null
        }
        Log.d("marysya", "onFabClicked: currentTripId = $currentTripId, destinationRoute = $destinationRoute")
        destinationRoute?.let { route -> navController.navigate(route = route) }
    }
}
