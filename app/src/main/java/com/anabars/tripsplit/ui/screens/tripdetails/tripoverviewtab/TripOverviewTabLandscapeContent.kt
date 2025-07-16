package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.ui.model.ExpenseCategory

@Composable
fun TripOverviewTabLandscapeContent(
    tripDetails: TripWithDetails?,
    exchangeRatesAvailable: Boolean,
    categorizedExpenses: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        val modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
        TripDataCard(tripDetails = tripDetails, modifier = modifier)
        ExpenseStatisticsCard(
            exchangeRatesAvailable = exchangeRatesAvailable,
            categorizedExpenses = categorizedExpenses,
            modifier = modifier
        )
    }
}