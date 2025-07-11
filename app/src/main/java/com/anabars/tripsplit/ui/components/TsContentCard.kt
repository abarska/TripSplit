package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun TsContentCard(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.border(
            width = if (isError) 2.dp else 1.dp,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius))
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_small)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}