package com.anabars.tripsplit.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anabars.tripsplit.R
import com.anabars.tripsplit.navigation.AppScreens
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.viewmodels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TsToolbar(
    navController: NavController,
    sharedState: SharedViewModel.SharedUiState,
    onDrawerToggle: () -> Unit
) {
    val startDestination = AppScreens.TRIPS.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    TopAppBar(
        title = {
            TsInfoText(
                text = sharedState.tabTitle ?: stringResource(R.string.app_name),
                fontSize = TsFontSize.MEDIUM
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        navigationIcon = {
            TsNavigationIcon(
                isStartDestination = currentRoute == startDestination,
                onMenuClick = onDrawerToggle,
                onBackClick = { sharedState.upButtonAction?.invoke() ?: navController.navigateUp() }
            )
        },
        actions = {
            sharedState.toolbarActions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = stringResource(action.contentDescriptionRes)
                    )
                }
            }
        },
    )
}
