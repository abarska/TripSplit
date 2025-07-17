package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R

@Composable
fun TripSettlementsTab(onTabTitleChange: (String) -> Unit, modifier: Modifier = Modifier) {

    val screenTitle = String.format(
        "%s: %s",
        stringResource(R.string.title_trip_details),
        stringResource(R.string.title_tab_settlements)
    )
    LaunchedEffect(Unit) {
        onTabTitleChange(screenTitle)
    }
}