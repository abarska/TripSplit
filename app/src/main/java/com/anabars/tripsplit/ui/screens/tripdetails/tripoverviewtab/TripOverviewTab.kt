package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.viewmodels.TripOverviewViewModel

@Composable
fun TripOverviewTab() {

    val viewModel: TripOverviewViewModel = hiltViewModel()
    val tripDetails by viewModel.tripDetails.collectAsState()
    val categorizedExpenses by viewModel.categorizedExpenses.collectAsState()
    val exchangeRatesAvailable by viewModel.areExchangeRatesAvailable.collectAsState()

    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        TripOverviewTabPortraitContent(
            tripDetails = tripDetails,
            exchangeRatesAvailable = exchangeRatesAvailable,
            categorizedExpenses = categorizedExpenses
        )
    } else {
        TripOverviewTabLandscapeContent(
            tripDetails = tripDetails,
            exchangeRatesAvailable = exchangeRatesAvailable,
            categorizedExpenses = categorizedExpenses
        )
    }
}