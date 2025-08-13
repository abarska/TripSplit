package com.anabars.tripsplit.ui.screens.tripdetails

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.model.TripDetailsTabs
import com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab.TripBalancesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripexpensestab.TripExpensesTab
import com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab.TripOverviewTab
import com.anabars.tripsplit.ui.screens.tripdetails.trippaymentstab.TripPaymentsTab

@Composable
fun TripDetailsScreen(
    selectedTabIndex: Int?,
    onTabChanged: (Int?) -> Unit,
    onTabTitleChange: (String) -> Unit,
    onTabActionsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(
        initialPage = selectedTabIndex ?: 1,
        pageCount = { TripDetailsTabs.size }
    )
    val context = LocalContext.current

    LaunchedEffect(selectedTabIndex) {
        updateToolbarForTab(
            context = context,
            index = selectedTabIndex,
            onTabTitleChange = onTabTitleChange,
            onTabActionsChange = onTabActionsChange,
        )
        if (selectedTabIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(selectedTabIndex ?: 1)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (selectedTabIndex != pagerState.currentPage) {
            onTabChanged(pagerState.currentPage)
        }
    }

    DisposableEffect(Unit) {
        onDispose { onTabChanged(null) }
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

private fun updateToolbarForTab(
    context: Context,
    index: Int?,
    onTabTitleChange: (String) -> Unit,
    onTabActionsChange: (Int) -> Unit
) {
    onTabTitleChange(getTabTitle(index ?: 1, context))
    onTabActionsChange(index ?: 1)
}

private fun getTabTitle(index: Int, context: Context): String {
    val prefixRes = R.string.title_trip_details
    val suffixRes = TripDetailsTabs[index].titleRes
    return "${context.getString(prefixRes)}: ${context.getString(suffixRes)}"
}