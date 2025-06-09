package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.anabars.tripsplit.R

@Composable
fun TripDetailsScreen(
    navController: NavController,
    tripId: Long,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(R.string.overview_tab, R.string.expenses_tab, R.string.settlements_tab)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(stringResource(title)) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> TripOverviewTab(tripId)
            1 -> TripExpensesTab(tripId)
            2 -> TripSettlementsTab(tripId)
        }
    }
}