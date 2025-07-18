package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TripSummaryCard(tripDetails: TripWithDetails?, modifier: Modifier = Modifier) {
    TsContentCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .inputWidthModifier()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tripDetails?.let {
                TsInfoText(
                    text = tripDetails.trip.title,
                    fontSize = TsFontSize.LARGE
                )
                TsInfoText(
                    text = "Participants: ${tripDetails.participants.joinToString { it.name }}",
                    fontSize = TsFontSize.MEDIUM
                )
                TsInfoText(
                    text = "Currencies: ${tripDetails.currencies.joinToString { it.code }}",
                    fontSize = TsFontSize.MEDIUM
                )
            }
        }
    }
}