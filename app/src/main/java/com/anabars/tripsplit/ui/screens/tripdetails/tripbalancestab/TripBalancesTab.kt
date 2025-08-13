package com.anabars.tripsplit.ui.screens.tripdetails.tripbalancestab

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun TripBalancesTab(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.under_construction),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )
}