package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun TripSplitRadioButton(
    value: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    ElevatedCard(
        modifier = modifier
            .padding(4.dp)
            .clickable { onItemClick(value) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor
        )
    ) {
        content()
    }
}