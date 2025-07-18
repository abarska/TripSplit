package com.anabars.tripsplit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.screens.AppScreens
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.viewmodels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TsToolbar(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    val startDestination = AppScreens.ROUTE_TRIPS
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tabTitle by sharedViewModel.tabTitle.collectAsState()

    TopAppBar(
        title = {
            TsInfoText(
                text = tabTitle ?: stringResource(R.string.app_name),
                fontSize = TsFontSize.MEDIUM
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        navigationIcon = {
            if (currentRoute == startDestination) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            } else {
                IconButton(onClick = {
                    if (!sharedViewModel.handleBack()) {
                        navController.navigateUp()
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}
