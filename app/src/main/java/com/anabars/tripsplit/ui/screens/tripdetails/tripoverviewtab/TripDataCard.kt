package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anabars.tripsplit.data.room.model.TripWithDetails
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText

@Composable
fun TripDataCard(tripDetails: TripWithDetails?, modifier: Modifier = Modifier) {
    TsContentCard(modifier = modifier) {
        tripDetails?.let {
            TsInfoText(text = tripDetails.trip.title, isHeader = true)
            TsInfoText(text = "Participants: ${tripDetails.participants.joinToString { it.name }}")
            TsInfoText(text = "Currencies: ${tripDetails.currencies.joinToString { it.code }}")
        }
    }
}