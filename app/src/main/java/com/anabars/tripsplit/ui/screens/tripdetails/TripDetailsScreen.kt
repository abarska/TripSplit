package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab.TripBalancesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab.TripExpensesTab
import com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab.TripPaymentsTab

@Composable
fun TripDetailsScreen(
    selectedTabItem: TabItem,
    onTabChanged: (TabItem) -> Unit,
    modifier: Modifier = Modifier
) {

    val allTabs = remember { TabItem.allTabs() }

    val pagerState = rememberPagerState(
        initialPage = allTabs.indexOf(selectedTabItem).coerceAtLeast(0),
        pageCount = { allTabs.size }
    )

    LaunchedEffect(selectedTabItem) {
        val targetPage = allTabs.indexOf(selectedTabItem).coerceAtLeast(0)
        if (targetPage != pagerState.currentPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val newTab = allTabs.getOrNull(pagerState.currentPage)
        if (newTab != null && newTab != selectedTabItem) {
            onTabChanged(newTab)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        when (allTabs[page]) {
            TabItem.Expenses -> TripExpensesTab()
            TabItem.Payments -> TripPaymentsTab()
            TabItem.Balances -> TripBalancesTab()
        }
    }
}