package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anabars.tripsplit.R

@Composable
fun TripExpensesTab(tripId: Long, modifier: Modifier = Modifier) {
    Text(stringResource(R.string.expenses_tab))
}