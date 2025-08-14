package com.anabars.tripsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anabars.tripsplit.navigation.AppNavGraph
import com.anabars.tripsplit.navigation.AppScreens
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

@Preview(showBackground = true)
@Composable
fun MainScreenWithDrawer() {

    val sharedViewModel: SharedViewModel = hiltViewModel()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val sharedUiState by sharedViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        sharedViewModel.uiEffect.collectLatest { effect ->
            if (effect is ShowSnackBar) {
                snackbarHostState.showSnackbar(context.getString(effect.resId))
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            }
        },
        topBar = {
            TsToolbar(
                navController = navController,
                sharedState = sharedViewModel.uiState,
                coroutineScope = coroutineScope,
                drawerState = drawerState
            )
        },
        bottomBar = {
            if (currentRoute?.startsWith(AppScreens.TRIP_DETAILS.route) == true) {
                TsBottomTabs(sharedUiState.selectedTabIndex) {
                    sharedViewModel.onEvent(SharedViewModel.SharedUiEvent.SetTabIndex(it))
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )
            ) {
                AppNavGraph(
                    navController = navController,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}
