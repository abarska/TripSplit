package com.anabars.tripsplit.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.*
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.*
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetFabVisibility
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabTitle
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetToolbarActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CollectNavigationEffects(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    sharedUiState: SharedUiState,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    // Navigate when tripId changes
    LaunchedEffect(sharedUiState.currentTripId) {
        sharedUiState.currentTripId?.let {
            navController.navigate(AppScreens.TRIP_DETAILS.route + "/$it") {
                launchSingleTop = true
            }
        }
    }

    // Update FAB, title, and toolbar actions
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(
        key1 = navBackStackEntry?.destination?.route,
        key2 = sharedUiState.selectedTabItem
    ) {
        val currentRoute = navBackStackEntry?.destination?.route
        val tripId = navBackStackEntry?.arguments?.getString("tripId")

        updateFabVisibility(currentRoute, sharedUiState.selectedTabItem) {
            sharedViewModel.onEvent(SetFabVisibility(it))
        }

        updateScreenTitle(currentRoute, tripId, sharedUiState.selectedTabItem, context) {
            sharedViewModel.onEvent(SetTabTitle(it))
        }

        updateToolbarActions(currentRoute, navController, sharedUiState.currentTripId) {
            sharedViewModel.onEvent(SetToolbarActions(it))
        }
    }

    // Handle one-time UI effects
    LaunchedEffect(Unit) {
        sharedViewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is ShowSnackBar -> snackbarHostState.showSnackbar(
                    context.getString(effect.resId)
                )

                is FabClicked -> onFabClicked(
                    navController = navController,
                    currentTripId = sharedUiState.currentTripId,
                    selectedTabItem = sharedUiState.selectedTabItem
                )
            }
        }
    }
}
