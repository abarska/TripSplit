package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

@Composable
fun TripOverviewTabPortraitContent(
    tripDetails: TripDetails?,
    expenseCategorizationResult: ExpenseCategorizationResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        TripSummaryCard(
            tripDetails = tripDetails,
            modifier = Modifier.fillMaxWidth()
        )
        ExpenseStatisticsCard(
            expenseCategorizationResult = expenseCategorizationResult,
            isPortrait = true,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}