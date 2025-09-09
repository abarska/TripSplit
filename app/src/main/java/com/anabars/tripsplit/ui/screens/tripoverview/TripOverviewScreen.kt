package com.anabars.tripsplit.ui.screens.tripoverview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.viewmodels.TripOverviewViewModel

@Composable
fun TripOverviewScreen(modifier: Modifier = Modifier) {

    val viewModel: TripOverviewViewModel = hiltViewModel()
    val tripDetails by viewModel.tripDetails.collectAsState()
    val expenseCategorizationResult by viewModel.expenseCategorizationResult.collectAsState()

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