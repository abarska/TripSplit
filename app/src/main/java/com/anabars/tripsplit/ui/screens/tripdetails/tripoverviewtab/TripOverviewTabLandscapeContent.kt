package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

@Composable
fun TripOverviewTabLandscapeContent(
    tripDetails: TripDetails?,
    expenseCategorizationResult: ExpenseCategorizationResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        TripSummaryCard(
            tripDetails = tripDetails,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
                .verticalScroll(rememberScrollState())
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