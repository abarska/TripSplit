package com.anabars.tripsplit.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.screens.AppScreens
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
        DrawerNavItem(
            R.string.title_trips,
            AppScreens.ROUTE_TRIPS,
            currentRoute,
            navController,
            coroutineScope,
            drawerState
        )
        DrawerNavItem(
            R.string.title_join_existing_trip,
            AppScreens.ROUTE_EXISTING_TRIP,
            currentRoute,
            navController,
            coroutineScope,
            drawerState
        )
        DrawerNavItem(
            R.string.title_settings,
            AppScreens.ROUTE_SETTINGS,
            currentRoute,
            navController,
            coroutineScope,
            drawerState
        )
    }
}

@Composable
fun DrawerNavItem(
    labelRes: Int,
    route: String,
    currentRoute: String?,
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    NavigationDrawerItem(
        label = { Text(stringResource(labelRes)) },
        selected = currentRoute == route,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RectangleShape,
        onClick = {
            coroutineScope.launch { drawerState.close() }
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}