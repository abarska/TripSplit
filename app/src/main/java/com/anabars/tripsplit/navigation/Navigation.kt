package com.anabars.tripsplit.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseScreen
import com.anabars.tripsplit.ui.screens.addpayment.AddPaymentScreen
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.archive.ArchiveScreen
import com.anabars.tripsplit.ui.screens.settings.SettingsScreen
import com.anabars.tripsplit.ui.screens.tripdetails.TripDetailsScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.ROUTE_TRIPS,
        modifier = modifier.fillMaxSize()
    ) {

        val onTabTitleChange: (String) -> Unit = {
            sharedViewModel.onEvent(SharedUiEvent.SetTabTitle(it))
        }

        composable(route = AppScreens.ROUTE_TRIPS) {
            TripsScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                setToolbarActions = { sharedViewModel.onEvent(SharedUiEvent.SetToolbarActions(it)) },
                modifier = modifier
            )
        }

        composable(
            route = "${AppScreens.ROUTE_ADD_TRIP}?tripId={tripId}", // optional parameter
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.StringType // passing a string because long arg is not nullable
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            AddTripScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
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
                onShowSnackbar = { resId: Int ->
                    sharedViewModel.onEffect(SharedViewModel.SharedUiEffect.ShowSnackBar(resId))
                },
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
            )
        }

        composable(
            route = AppScreens.ROUTE_ADD_PAYMENT + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getLong("tripId") ?: return@composable
            AddPaymentScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
            )
        }

        composable(route = AppScreens.ROUTE_SETTINGS) {
            SettingsScreen(
                onTabTitleChange = onTabTitleChange,
                modifier = modifier
            )
        }

        composable(route = AppScreens.ROUTE_ARCHIVE) {
            ArchiveScreen(
                onTabTitleChange = onTabTitleChange,
                navController = navController,
                modifier = modifier
            )
        }

        composable(
            route = AppScreens.ROUTE_TRIP_DETAILS + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val tripId = backStackEntry.arguments?.getLong("id")
            if (tripId == null) return@composable

            val sharedUiState by sharedViewModel.uiState.collectAsState()
            val onTabActionsChange = { index: Int ->
                sharedViewModel.onEvent(
                    SharedUiEvent.SetToolbarActions(
                        if (index == 0) listOf(
                            ActionButton.ToolbarActionButton(
                                icon = Icons.Default.Edit,
                                contentDescriptionRes = R.string.edit_item,
                                onClick = { navController.navigate("${AppScreens.ROUTE_ADD_TRIP}?tripId=$tripId") }
                            )
                        ) else emptyList()))
            }

            TripDetailsScreen(
                navController = navController,
                selectedTabIndex = sharedUiState.selectedTabIndex,
                onTabChanged = { sharedViewModel.onEvent(SharedUiEvent.SetTabIndex(it)) },
                onTabTitleChange = onTabTitleChange,
                onTabActionsChange = onTabActionsChange
            )
        }
    }
}