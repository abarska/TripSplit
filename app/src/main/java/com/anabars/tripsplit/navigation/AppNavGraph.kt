package com.anabars.tripsplit.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.model.TripDetailsTabs
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
    val sharedUiState by sharedViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sharedUiState.currentTripId) {
        sharedUiState.currentTripId?.let {
            navController.navigate(AppScreens.TRIP_DETAILS.route + "/$it")
        }
    }

    LaunchedEffect(
        key1 = navBackStackEntry?.destination?.route,
        key2 = sharedUiState.selectedTabIndex
    ) {
        updateFabVisibility(
            navBackStackEntry = navBackStackEntry,
            index = sharedUiState.selectedTabIndex
        ) {
            sharedViewModel.onEvent(SetFabVisibility(it))
        }
        updateScreenTitle(
            navBackStackEntry = navBackStackEntry,
            index = sharedUiState.selectedTabIndex,
            context = context
        ) {
            sharedViewModel.onEvent(SetTabTitle(it))
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
        startDestination = AppScreens.TRIPS.route,
        modifier = modifier.fillMaxSize()
    ) {

        val onShowSnackbar: (Int) -> Unit = {
            sharedViewModel.onEffect(ShowSnackBar(it))
        }

        composable(route = AppScreens.TRIPS.route) {
            TripsScreen(
                onTripSelected = { sharedViewModel.onEvent(SetCurrentTrip(it)) },
                setToolbarActions = { sharedViewModel.onEvent(SetToolbarActions(it)) },
                modifier = modifier
            )
        }

        composable(
            route = "${AppScreens.ADD_TRIP.route}?tripId={tripId}", // optional parameter
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
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
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
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
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
                setBackHandler = { action -> sharedViewModel.setBackHandler(action) }
            )
        }

        composable(route = AppScreens.SETTINGS.route) {
            SettingsScreen(modifier = modifier)
        }

        composable(route = AppScreens.ARCHIVE.route) {
            ArchiveScreen(
                navController = navController,
                modifier = modifier
            )
        }

        composable(
            route = AppScreens.TRIP_DETAILS.route + "/{id}",
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
                                onClick = { navController.navigate("${AppScreens.ADD_TRIP.route}?tripId=$tripId") }
                            )
                        ) else emptyList()))
            }

            TripDetailsScreen(
                selectedTabIndex = sharedUiState.selectedTabIndex,
                onTabChanged = { sharedViewModel.onEvent(SetTabIndex(it)) },
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
            currentRoute.startsWith(AppScreens.TRIPS.route) -> AppScreens.ADD_TRIP.route

            currentRoute.startsWith(AppScreens.TRIP_DETAILS.route) -> {
                when (index) {
                    1 -> "${AppScreens.ADD_EXPENSE.route}/${currentTripId}/${AddItemViewModel.UseCase.EXPENSE.name}"
                    2 -> "${AppScreens.ADD_PAYMENT.route}/${currentTripId}/${AddItemViewModel.UseCase.PAYMENT.name}"
                    else -> null
                }
            }

            else -> null
        }
        destinationRoute?.let { route -> navController.navigate(route = route) }
    }
}

private fun updateScreenTitle(
    navBackStackEntry: NavBackStackEntry?,
    index: Int?,
    context: Context,
    onTabTitleChange: (String) -> Unit
) {
    val currentRoute = navBackStackEntry?.destination?.route
    val tripIdArg = navBackStackEntry?.arguments?.getString("tripId")
    currentRoute?.let { route ->
        val screenTitle = when {
            route.startsWith(AppScreens.TRIPS.route) -> context.getString(R.string.title_trips)
            route.startsWith(AppScreens.ADD_EXPENSE.route) -> context.getString(R.string.title_new_expense)
            route.startsWith(AppScreens.ADD_PAYMENT.route) -> context.getString(R.string.title_new_payment)
            route.startsWith(AppScreens.SETTINGS.route) -> context.getString(R.string.title_settings)
            route.startsWith(AppScreens.ARCHIVE.route) -> context.getString(R.string.title_archive)

            route.startsWith(AppScreens.ADD_TRIP.route) -> {
                if (tripIdArg.isNullOrEmpty()) context.getString(R.string.title_new_trip)
                else context.getString(R.string.title_edit_trip)
            }

            route.startsWith(AppScreens.TRIP_DETAILS.route) -> {
                val prefixRes = R.string.title_trip_details
                val suffixRes = TripDetailsTabs[index ?: 1].titleRes
                "${context.getString(prefixRes)}: ${context.getString(suffixRes)}"
            }

            else -> throw IllegalStateException("Unknown route: $route")
        }
        onTabTitleChange(screenTitle)
    }
}

private fun updateFabVisibility(
    navBackStackEntry: NavBackStackEntry?,
    index: Int?,
    updateFabVisibility: (Boolean) -> Unit
) {
    val currentRoute = navBackStackEntry?.destination?.route
    val fabVisible = when {
        currentRoute == AppScreens.TRIPS.route -> true
        currentRoute?.startsWith(AppScreens.TRIP_DETAILS.route) == true -> index in listOf(1, 2)
        else -> false
    }
    updateFabVisibility(fabVisible)
}