package com.anabars.tripsplit.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.navigation.screens.AppScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val drawerWidthFraction = if (isLandscape) 0.35f else 0.65f
    Column(
        modifier = modifier
            .fillMaxWidth(drawerWidthFraction)
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.title_home)) },
            selected = currentRoute == AppScreens.ROUTE_HOME,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate(AppScreens.ROUTE_HOME) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.title_trips)) },
            selected = currentRoute?.startsWith(AppScreens.ROUTE_TRIPS) == true,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate(AppScreens.ROUTE_TRIPS + "/someId") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.title_settings)) },
            selected = currentRoute == AppScreens.ROUTE_SETTINGS,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate(AppScreens.ROUTE_SETTINGS) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}