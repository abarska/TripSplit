package com.anabars.tripsplit.ui.screens.tripdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.viewmodels.TripOverviewViewModel

@Composable
fun TripOverviewTab(modifier: Modifier = Modifier) {

    val viewModel: TripOverviewViewModel = hiltViewModel()
    val tripDetails by viewModel.tripDetails.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (tripDetails == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            Text(
                text = stringResource(R.string.loading_trip_details),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 80.dp)
            )
        } else {
            val trip = tripDetails!!.trip
            val participants = tripDetails!!.participants
            val currencies = tripDetails!!.currencies

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoText(text = trip.title, isHeader = true)
                InfoText(text = "Participants: ${participants.joinToString { it.name }}")
                InfoText(text = "Currencies: ${currencies.joinToString { it.code }}")
            }
        }
    }
}