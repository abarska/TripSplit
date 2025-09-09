package com.anabars.tripsplit.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.ui.screens.additem.AddExpenseScreen
import com.anabars.tripsplit.ui.screens.additem.AddPaymentScreen
import com.anabars.tripsplit.ui.screens.addtrip.AddTripScreen
import com.anabars.tripsplit.ui.screens.archive.ArchiveScreen
import com.anabars.tripsplit.ui.screens.settings.SettingsScreen
import com.anabars.tripsplit.ui.screens.tripdetails.TripDetailsScreen
import com.anabars.tripsplit.ui.screens.tripoverview.TripOverviewScreen
import com.anabars.tripsplit.ui.screens.trips.TripsScreen
import com.anabars.tripsplit.viewmodels.AddItemViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.FabClicked
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.ShowSnackBar
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetCurrentTrip
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetFabVisibility
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabItem
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabTitle
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetToolbarActions
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.UpdateUpButtonAction
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

    val updateUpButtonAction: ((() -> Unit)?) -> Unit =
        { sharedViewModel.onEvent(UpdateUpButtonAction(it)) }

    LaunchedEffect(sharedUiState.currentTripId) {
        sharedUiState.currentTripId?.let {
            navController.navigate(AppScreens.TRIP_DETAILS.route + "/$it") {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(
        key1 = navBackStackEntry?.destination?.route,
        key2 = sharedUiState.selectedTabItem
    ) {

        updateFabVisibility(
            currentRoute = navBackStackEntry?.destination?.route,
            selectedTabItem = sharedUiState.selectedTabItem
        ) {
            sharedViewModel.onEvent(SetFabVisibility(it))
        }

        updateScreenTitle(
            currentRoute = navBackStackEntry?.destination?.route,
            tripId = navBackStackEntry?.arguments?.getString("tripId"),
            selectedTabItem = sharedUiState.selectedTabItem,
            context = context
        ) {
            sharedViewModel.onEvent(SetTabTitle(it))
        }

        updateToolbarActions(
            currentRoute = navBackStackEntry?.destination?.route,
            navController = navController,
            tripId = sharedViewModel.uiState.value.currentTripId
        ) {
            sharedViewModel.onEvent(SetToolbarActions(it))
        }
    }

    LaunchedEffect(Unit) {
        sharedViewModel.uiEffect.collectLatest {
            if (it is FabClicked) {
                onFabClicked(
                    navController = navController,
                    currentTripId = sharedViewModel.uiState.value.currentTripId,
                    selectedTabItem = sharedUiState.selectedTabItem
                )
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreens.TRIPS.route,
        modifier = modifier
    ) {

        val onShowSnackbar: (Int) -> Unit = {
            sharedViewModel.onEffect(ShowSnackBar(it))
        }

        composable(route = AppScreens.TRIPS.route) {
            TripsScreen(
                onTripSelected = { sharedViewModel.onEvent(SetCurrentTrip(it)) }
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
            route = AppScreens.TRIP_DETAILS.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->

            val tripId = backStackEntry.arguments?.getLong("id")
            if (tripId == null) return@composable

            val sharedUiState by sharedViewModel.uiState.collectAsState()

            TripDetailsScreen(
                selectedTabItem = sharedUiState.selectedTabItem,
                onTabChanged = { sharedViewModel.onEvent(SetTabItem(it)) }
            )
        }

        composable(
            route = AppScreens.TRIP_OVERVIEW.route + "/{id}",
            arguments = listOf(navArgument(name = "id") { type = NavType.LongType })
        )
        { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("id")
            if (tripId == null) return@composable
            TripOverviewScreen(modifier = Modifier.padding(16.dp))
        }
    }
}

private fun onFabClicked(
    navController: NavHostController,
    currentTripId: Long?,
    selectedTabItem: TabItem
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
        ?: navController.graph.startDestinationRoute
    currentRoute?.let { currentRoute ->
        val destinationRoute = when {
            currentRoute.startsWith(AppScreens.TRIPS.route) -> AppScreens.ADD_TRIP.route

            currentRoute.startsWith(AppScreens.TRIP_DETAILS.route) -> {
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
        destinationRoute?.let { route -> navController.navigate(route = route) }
    }
}

private fun updateToolbarActions(
    currentRoute: String?,
    navController: NavHostController,
    tripId: Long?,
    onTabActionsChange: (List<ActionButton.ToolbarActionButton>) -> Unit,
) {
    currentRoute?.let {
        val actions = when {
            currentRoute.startsWith(AppScreens.TRIP_DETAILS.route) ->
                getActionsForTripDetails(tripId, navController)

            currentRoute.startsWith(AppScreens.TRIP_OVERVIEW.route) ->
                getActionsForTripOverview(tripId, navController)

            else -> emptyList()
        }
        onTabActionsChange(actions)
    }
}

private fun getActionsForTripDetails(tripId: Long?, navController: NavHostController) =
    listOf(
        ActionButton.ToolbarActionButton(
            icon = Icons.Outlined.Info,
            contentDescriptionRes = R.string.title_overview,
            onClick = {
                tripId?.let { navController.navigate("${AppScreens.TRIP_OVERVIEW.route}/$it") }
            }
        ))

private fun getActionsForTripOverview(tripId: Long?, navController: NavHostController) =
    listOf(
        ActionButton.ToolbarActionButton(
            icon = Icons.Default.Edit,
            contentDescriptionRes = R.string.edit_item,
            onClick = {
                tripId?.let { navController.navigate("${AppScreens.ADD_TRIP.route}?tripId=$it") }
            })
    )

private fun updateScreenTitle(
    currentRoute: String?,
    tripId: String?,
    selectedTabItem: TabItem,
    context: Context,
    onTabTitleChange: (String) -> Unit
) {
    currentRoute?.let { route ->
        val screenTitle = when {
            route.startsWith(AppScreens.TRIPS.route) -> context.getString(R.string.title_trips)
            route.startsWith(AppScreens.ADD_EXPENSE.route) -> context.getString(R.string.title_new_expense)
            route.startsWith(AppScreens.ADD_PAYMENT.route) -> context.getString(R.string.title_new_payment)
            route.startsWith(AppScreens.SETTINGS.route) -> context.getString(R.string.title_settings)
            route.startsWith(AppScreens.ARCHIVE.route) -> context.getString(R.string.title_archive)
            route.startsWith(AppScreens.TRIP_OVERVIEW.route) -> context.getString(R.string.title_overview)

            route.startsWith(AppScreens.ADD_TRIP.route) -> {
                if (tripId.isNullOrEmpty()) context.getString(R.string.title_new_trip)
                else context.getString(R.string.title_edit_trip)
            }

            route.startsWith(AppScreens.TRIP_DETAILS.route) -> {
                val prefixRes = R.string.title_trip_details
                val suffixRes = selectedTabItem.titleRes
                "${context.getString(prefixRes)}: ${context.getString(suffixRes)}"
            }

            else -> throw IllegalStateException("Unknown route: $route")
        }
        onTabTitleChange(screenTitle)
    }
}

private fun updateFabVisibility(
    currentRoute: String?,
    selectedTabItem: TabItem,
    updateFabVisibility: (Boolean) -> Unit
) {
    val fabVisible = when {
        currentRoute == AppScreens.TRIPS.route -> true
        currentRoute?.startsWith(AppScreens.TRIP_DETAILS.route) == true ->
            selectedTabItem in setOf(TabItem.Expenses, TabItem.Payments)

        else -> false
    }
    updateFabVisibility(fabVisible)
}