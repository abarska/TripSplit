package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize

@Composable
fun TripSummaryCard(tripDetails: TripWithDetails?, modifier: Modifier = Modifier) {
    TsContentCard(modifier = modifier) {
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