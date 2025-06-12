package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.TripSplitTab

@Composable
fun TripDetailsScreen(
    navController: NavController,
    tripId: Long,
    modifier: Modifier = Modifier
) {
    val tabs = tripDetailsTabs()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    val contentColor =
                        LocalContentColor.current.copy(alpha = if (isSelected) 1.0f else 0.6f)
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(text = stringResource(tab.titleRes), color = contentColor)
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.contentDescriptionRes),
                                tint = contentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }
            }
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

fun tripDetailsTabs() = listOf(
    TripSplitTab(
        icon = Icons.Outlined.Home,
        titleRes = R.string.overview_tab,
        contentDescriptionRes = R.string.overview_tab_content_description,
    ),
    TripSplitTab(
        icon = Icons.Outlined.AttachMoney,
        titleRes = R.string.expenses_tab,
        contentDescriptionRes = R.string.expenses_tab_content_description,
    ),
    TripSplitTab(
        icon = Icons.AutoMirrored.Outlined.CompareArrows,
        titleRes = R.string.settlements_tab,
        contentDescriptionRes = R.string.settlements_tab_content_description,
    )
)
