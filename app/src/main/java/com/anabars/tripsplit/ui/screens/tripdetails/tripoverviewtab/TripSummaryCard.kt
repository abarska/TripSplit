package com.anabars.tripsplit.ui.screens.tripdetails.tripoverviewtab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripWithDetails
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TripSummaryCard(tripDetails: TripDetails?, modifier: Modifier = Modifier) {
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
                    fontSize = TsFontSize.LARGE,
                    textAlign = TextAlign.Center
                )
                TsInfoText(
                    text = "${stringResource(R.string.participants)}: ${tripDetails.participants.joinToString { it.name }}",
                    fontSize = TsFontSize.MEDIUM,
                    textAlign = TextAlign.Center
                )
                TsInfoText(
                    text = "${stringResource(R.string.currencies)}: ${tripDetails.currencies.joinToString { it.code }}",
                    fontSize = TsFontSize.MEDIUM,
                    textAlign = TextAlign.Center
                )
                TsInfoText(
                    text = "${stringResource(R.string.status)}: ${stringResource(tripDetails.trip.status.labelRes)}",
                    fontSize = TsFontSize.MEDIUM,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun TripSummaryCardPreview() {
    TripSummaryCard(
        tripDetails = getFakeTripWithDetails(),
        modifier = Modifier.inputWidthModifier()
    )

}