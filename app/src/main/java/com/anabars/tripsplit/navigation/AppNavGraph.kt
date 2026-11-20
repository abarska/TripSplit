package com.anabars.tripsplit.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anabars.tripsplit.ui.screens.additem.AddExpenseScreen
import com.anabars.tripsplit.ui.screens.additem.AddPaymentScreen
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.archive.ArchiveScreen
import com.anabars.tripsplit.ui.screens.settings.SettingsScreen
import com.anabars.tripsplit.ui.screens.tripdetails.TripDetailsScreen
import com.anabars.tripsplit.ui.screens.tripoverview.TripOverviewScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.ShowSnackBar
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabItem
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.UpdateUpButtonAction

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
) {

    val sharedUiState by sharedViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectNavigationEffects(
        navController = navController,
        sharedViewModel = sharedViewModel,
        snackbarHostState = snackbarHostState
    )

    NavHost(
        navController = navController,
        startDestination = AppScreens.TRIPS.route,
        modifier = modifier
    ) {

        val onShowSnackbar: (Int) -> Unit =
            { sharedViewModel.onEffect(ShowSnackBar(it)) }

        val updateUpButtonAction: ((() -> Unit)?) -> Unit =
            { sharedViewModel.onEvent(UpdateUpButtonAction(it)) }

        composable(route = AppScreens.TRIPS.route) {
            TripsScreen(
                onTripSelected = { tripId ->
                    navController.navigate("${AppScreens.TRIP_DETAILS.route}/$tripId")
                }
            )
        }

        composable(route = AppScreens.ADD_TRIP.route) {
            AddTripScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                updateUpButtonAction = updateUpButtonAction
            )
        }

        composable(
            route = AppScreens.ADD_TRIP.route + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) {
            AddTripScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                updateUpButtonAction = updateUpButtonAction
            )
        }

        composable(
            route = AppScreens.ADD_EXPENSE.route + "/{tripId}/{useCase}",
            arguments = listOf(
                navArgument(name = "tripId") { type = NavType.LongType },
                navArgument("useCase") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddExpenseScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                updateUpButtonAction = updateUpButtonAction
            )
        }

        composable(
            route = AppScreens.ADD_PAYMENT.route + "/{tripId}/{useCase}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("useCase") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddPaymentScreen(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
                updateUpButtonAction = updateUpButtonAction
            )
        }

        composable(route = AppScreens.SETTINGS.route) {
            SettingsScreen()
        }

        composable(route = AppScreens.ARCHIVE.route) {
            ArchiveScreen(navController = navController)
        }

        composable(
            route = AppScreens.TRIP_DETAILS.route + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->

            val tripId = backStackEntry.arguments?.getLong("tripId")
            if (tripId == null) return@composable

            TripDetailsScreen(
                selectedTabItem = sharedUiState.selectedTabItem,
                onTabChanged = { sharedViewModel.onEvent(SetTabItem(it)) }
            )
        }

        composable(
            route = AppScreens.TRIP_OVERVIEW.route + "/{tripId}",
            arguments = listOf(navArgument(name = "tripId") { type = NavType.LongType })
        )
        { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId")
            if (tripId == null) return@composable
            TripOverviewScreen(modifier = Modifier.padding(16.dp))
        }
    }
}