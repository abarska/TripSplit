package com.anabars.tripsplit.ui.screens.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.model.Participant
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TripRowItem(
    trip: Trip,
    participants: List<Participant>,
    modifier: Modifier = Modifier,
    onItemClick: (Trip) -> Unit = {}
) {
    Column(
        modifier = modifier
            .then(
                Modifier
                    .inputWidthModifier()
                    .heightIn(dimensionResource(R.dimen.item_row_height))
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        InfoText(text = String.format("%s (%s)", trip.title, trip.description))
    }
}