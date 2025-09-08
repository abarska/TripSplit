package com.anabars.tripsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anabars.tripsplit.navigation.AppNavGraph
import com.anabars.tripsplit.navigation.AppScreens.Companion.isTripDetailsRoute
import com.anabars.tripsplit.ui.components.DrawerContent
import com.anabars.tripsplit.ui.components.TsPlusFab
import com.anabars.tripsplit.ui.components.TsToolbar
import com.anabars.tripsplit.ui.theme.AppTheme
import com.anabars.tripsplit.ui.widgets.TsBottomTabs
import com.anabars.tripsplit.viewmodels.SharedViewModel
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.FabClicked
import com.anabars.tripsplit.viewmodels.SharedViewModel.SharedUiEffect.ShowSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainScreenWithDrawer()
            }
        }
    }
}

@Composable
private fun MainScreenWithDrawer() {

    val sharedViewModel: SharedViewModel = hiltViewModel()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sharedUiState by sharedViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context by rememberUpdatedState(LocalContext.current)

    LaunchedEffect(sharedViewModel.uiEffect) {
        sharedViewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is ShowSnackBar -> snackbarHostState.showSnackbar(context.getString(effect.resId))
                is FabClicked -> {} // already handled via FAB
            }
        }
    }

    Scaffold(
        snackbarHost = { ErrorSnackbarHost(snackbarHostState) },
        topBar = {
            TsToolbar(
                navController = navController,
                sharedState = sharedUiState,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        },
        bottomBar = {
            if (currentRoute.isTripDetailsRoute()) {
                TsBottomTabs(selectedTabItem = sharedUiState.selectedTabItem) {
                    sharedViewModel.onEvent(SharedViewModel.SharedUiEvent.SetTabItem(it))
                }
            }
        },
        floatingActionButton = {
            if (sharedUiState.fabVisible) {
                TsPlusFab { sharedViewModel.onEffect(FabClicked) }
            }
        }
    ) { paddingValues ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    currentRoute = currentRoute,
                    coroutineScope = coroutineScope,
                    modifier = Modifier.padding(paddingValues = paddingValues)
                )
            },
        ) {
            AppNavGraph(
                navController = navController,
                sharedViewModel = sharedViewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun ErrorSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }
}