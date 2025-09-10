package com.anabars.tripsplit.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.FabClicked
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.ShowSnackBar
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetFabVisibility
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetTabTitle
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEvent.SetToolbarActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CollectNavigationEffects(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val sharedUiState by sharedViewModel.uiState.collectAsState()

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

        updateToolbarActions(currentRoute, navController, tripId?.toLongOrNull()) {
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
                    selectedTabItem = sharedUiState.selectedTabItem
                )
            }
        }
    }
}
