package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.viewmodels.TripOverviewViewModel

@Composable
fun TripOverviewTab(onTabTitleChange: (String) -> Unit, modifier: Modifier = Modifier) {

    val viewModel: TripOverviewViewModel = hiltViewModel()
    val tripDetails by viewModel.tripDetails.collectAsState()
    val expenseCategorizationResult by viewModel.expenseCategorizationResult.collectAsState()

    val screenTitle = String.format(
        "%s: %s",
        stringResource(R.string.title_trip_details),
        stringResource(R.string.title_tab_overview)
    )
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }

    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        TripOverviewTabPortraitContent(
            tripDetails = tripDetails,
            expenseCategorizationResult = expenseCategorizationResult,
            modifier = modifier
        )
    } else {
        TripOverviewTabLandscapeContent(
            tripDetails = tripDetails,
            expenseCategorizationResult = expenseCategorizationResult,
            modifier = modifier
        )
    }
}