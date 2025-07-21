package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab.TripExpensesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab.TripOverviewTab
import com.anabars.tripsplit.ui.widgets.TsBottomTabs

@Composable
fun TripDetailsScreen(
    navController: NavController,
    onTabTitleChange: (String) -> Unit,
    setToolbarActions: (List<ActionButton.ToolbarActionButton>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            TsBottomTabs(selectedTabIndex) { newIndex -> selectedTabIndex = newIndex }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> {
                    TripOverviewTab(
                        onTabTitleChange = onTabTitleChange,
                        setToolbarActions = setToolbarActions,
                        navController = navController,
                        modifier = modifier
                            .fillMaxSize()
                            .padding(all = 16.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding())
                    )
                }

                1 -> TripExpensesTab(
                    navController = navController,
                    onTabTitleChange = onTabTitleChange,
                    paddingValues = paddingValues
                )

                2 -> TripSettlementsTab(onTabTitleChange = onTabTitleChange)
            }
        }
    }
}
