package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.ui.model.ExpenseCategory

@Composable
fun TripOverviewTabPortraitContent(
    tripDetails: TripWithDetails?,
    exchangeRatesAvailable: Boolean,
    categorizedExpenses: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        TripDataCard(tripDetails = tripDetails, modifier = modifier)
        ExpenseStatisticsCard(
            exchangeRatesAvailable = exchangeRatesAvailable,
            categorizedExpenses = categorizedExpenses,
            modifier = modifier
        )
    }
}