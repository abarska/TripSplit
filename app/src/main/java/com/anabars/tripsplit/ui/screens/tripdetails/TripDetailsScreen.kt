package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab.TripExpensesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab.TripOverviewTab

@Composable
fun TripDetailsScreen(
    navController: NavController,
    selectedTabIndex: Int,
    onTabTitleChange: (String) -> Unit,
    setToolbarActions: (List<ActionButton.ToolbarActionButton>) -> Unit,
    modifier: Modifier = Modifier
) {

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
                )
            }

            1 -> TripExpensesTab(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
            )

            2 -> TripPaymentTab(
                navController = navController,
                onTabTitleChange = onTabTitleChange,
            )
        }
    }
}
