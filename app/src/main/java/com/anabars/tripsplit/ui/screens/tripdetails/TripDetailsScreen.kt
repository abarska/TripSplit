package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab.TripBalancesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab.TripExpensesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab.TripOverviewTab
import com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab.TripPaymentsTab

@Composable
fun TripDetailsScreen(
    selectedTabItem: TabItem,
    onTabChanged: (TabItem) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(
        initialPage = selectedTabItem.ordinal,
        pageCount = { TabItem.allTabs().size }
    )

    LaunchedEffect(selectedTabItem) {
        if (selectedTabItem.ordinal != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedTabItem.ordinal)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (selectedTabItem.ordinal != pagerState.currentPage) {
            val newTab = TabItem.allTabs().firstOrNull { it.ordinal == pagerState.currentPage }
            onTabChanged(newTab ?: TabItem.Expenses)
        }
    }

    DisposableEffect(Unit) {
        onDispose { onTabChanged(TabItem.Expenses) }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> TripOverviewTab(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
            )

            1 -> TripExpensesTab()
            2 -> TripPaymentsTab()
            3 -> TripBalancesTab()
        }
    }
}