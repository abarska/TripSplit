package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

@Composable
fun TripOverviewTabLandscapeContent(
    tripDetails: TripWithDetails?,
    expenseCategorizationResult: ExpenseCategorizationResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        TripSummaryCard(
            tripDetails = tripDetails,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        )
        ExpenseStatisticsCard(
            expenseCategorizationResult = expenseCategorizationResult,
            isPortrait = false,
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
        )
    }
}