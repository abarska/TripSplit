package com.anabars.tripsplit.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.screens.addexpense.AddExpenseScreen
import com.anabars.tripsplit.ui.screens.addpayment.AddPaymentScreen
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.archive.ArchiveScreen
import com.anabars.tripsplit.ui.screens.settings.SettingsScreen
import com.anabars.tripsplit.ui.screens.tripdetails.TripDetailsScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.FabClicked
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.ShowSnackBar
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetCurrentTrip
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetFabVisibility
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabIndex
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabTitle
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetToolbarActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sharedUiState by sharedViewModel.uiState.collectAsState()

    LaunchedEffect(sharedUiState.currentTripId) {
        sharedUiState.currentTripId?.let {
            navController.navigate(Routes.ROUTE_TRIP_DETAILS + "/$it")
        }
    }

    LaunchedEffect(currentRoute, sharedUiState.selectedTabIndex) {
        updateFabVisibility(currentRoute, sharedUiState.selectedTabIndex) {
            sharedViewModel.onEvent(SetFabVisibility(it))
        }
    }

    LaunchedEffect(Unit) {
        sharedViewModel.uiEffect.collectLatest {
            if (it is FabClicked) {
                onFabClicked(
                    navController = navController,
                    currentTripId = sharedViewModel.uiState.value.currentTripId,
                    index = sharedUiState.selectedTabIndex
                )
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.ROUTE_TRIPS,
        modifier = modifier.fillMaxSize()
    ) {

        val onTabTitleChange: (String) -> Unit = {
            sharedViewModel.onEvent(SetTabTitle(it))
        }
        val onShowSnackbar: (Int) -> Unit = {
            sharedViewModel.onEffect(ShowSnackBar(it))
        }

        composable(route = Routes.ROUTE_TRIPS) {
            TripsScreen(
                onTripSelected = { sharedViewModel.onEvent(SetCurrentTrip(it)) },
                onTabTitleChange = onTabTitleChange,
                setToolbarActions = { sharedViewModel.onEvent(SetToolbarActions(it)) },
                modifier = modifier
            )
        }

        composable(
            route = "${Routes.ROUTE_ADD_TRIP}?tripId={tripId}", // optional parameter
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
            route = Routes.ROUTE_ADD_EXPENSE + "/{tripId}/{useCase}",
            arguments = listOf(
                navArgument(name = "tripId") { type = NavType.LongType },
                navArgument("useCase") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddExpenseScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                onShowSnackbar = onShowSnackbar,
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
            )
        }

        composable(
            route = Routes.ROUTE_ADD_PAYMENT + "/{tripId}/{useCase}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType },
                navArgument("useCase") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddPaymentScreen(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
                onShowSnackbar = onShowSnackbar,
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
            )
        }

        composable(route = Routes.ROUTE_SETTINGS) {
            SettingsScreen(
                onTabTitleChange = onTabTitleChange,
                modifier = modifier
            )
        }

        composable(route = Routes.ROUTE_ARCHIVE) {
            ArchiveScreen(
                onTabTitleChange = onTabTitleChange,
                navController = navController,
                modifier = modifier
            )
        }

        composable(
            route = Routes.ROUTE_TRIP_DETAILS + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val tripId = backStackEntry.arguments?.getLong("id")
            if (tripId == null) return@composable

            val sharedUiState by sharedViewModel.uiState.collectAsState()
            val onTabActionsChange = { index: Int ->
                sharedViewModel.onEvent(
                    SetToolbarActions(
                        if (index == 0) listOf(
                            ActionButton.ToolbarActionButton(
                                icon = Icons.Default.Edit,
                                contentDescriptionRes = R.string.edit_item,
                                onClick = { navController.navigate("${Routes.ROUTE_ADD_TRIP}?tripId=$tripId") }
                            )
                        ) else emptyList()))
            }

            TripDetailsScreen(
                selectedTabIndex = sharedUiState.selectedTabIndex,
                onTabChanged = { sharedViewModel.onEvent(SetTabIndex(it)) },
                onTabTitleChange = onTabTitleChange,
                onTabActionsChange = onTabActionsChange
            )
        }
    }
}

private fun onFabClicked(navController: NavHostController, currentTripId: Long?, index: Int?) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
        ?: navController.graph.startDestinationRoute
    currentRoute?.let { currentRoute ->
        val destinationRoute = when {
            currentRoute.startsWith(Routes.ROUTE_TRIPS) -> Routes.ROUTE_ADD_TRIP

            currentRoute.startsWith(Routes.ROUTE_TRIP_DETAILS) -> {
                when (index) {
                    1 -> "${Routes.ROUTE_ADD_EXPENSE}/${currentTripId}/${AddItemViewModel.UseCase.EXPENSE.name}"
                    2 -> "${Routes.ROUTE_ADD_PAYMENT}/${currentTripId}/${AddItemViewModel.UseCase.PAYMENT.name}"
                    else -> null
                }
            }

            else -> null
        }
        destinationRoute?.let { route -> navController.navigate(route = route) }
    }
}

private fun updateFabVisibility(
    currentRoute: String?,
    index: Int?,
    updateFabVisibility: (Boolean) -> Unit
) {
    val fabVisible = when {
        currentRoute == Routes.ROUTE_TRIPS -> true
        currentRoute?.startsWith(Routes.ROUTE_TRIP_DETAILS) == true -> index in listOf(1, 2)
        else -> false
    }
    updateFabVisibility(fabVisible)
}