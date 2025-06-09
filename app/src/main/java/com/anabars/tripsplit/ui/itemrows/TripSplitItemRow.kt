package com.anabars.tripsplit.ui.itemrows

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun TripSplitItemRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onItemClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(dimensionResource(R.dimen.chip_corner_radius)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        enabled = enabled,
        onClick = onItemClick
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun TripSplitItemRowPreview() {
    TripSplitItemRow(onItemClick = {}) {
        Row(modifier = Modifier.padding(8.dp)) {
            ItemRowActionButton(ActionButton(icon = Icons.Default.Close) {})
            Spacer(modifier = Modifier.width(8.dp))
            InfoText(text = "placeholder")
        }
    }
}