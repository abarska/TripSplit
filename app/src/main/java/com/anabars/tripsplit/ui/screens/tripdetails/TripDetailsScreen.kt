package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab.TripOverviewTab
import com.anabars.tripsplit.ui.widgets.TsBottomTabs

@Composable
fun TripDetailsScreen(navController: NavController, modifier: Modifier = Modifier) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            TsBottomTabs(selectedTabIndex) { newIndex -> selectedTabIndex = newIndex }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> TripOverviewTab()
                1 -> TripExpensesTab(navController = navController, paddingValues = paddingValues)
                2 -> TripSettlementsTab()
            }
        }
    }
}
