package com.anabars.tripsplit.ui.listitems

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsItemRowActionButton
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun TsItemRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    onItemClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        enabled = enabled,
        onClick = onItemClick,
        colors =
            if (highlighted) CardDefaults.elevatedCardColors()
                .copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else CardDefaults.elevatedCardColors()
                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun TsItemRowPreview() {
    TsItemRow(onItemClick = {}) {
        Row(modifier = Modifier.padding(8.dp)) {
            TsItemRowActionButton(ActionButton.ChipActionButton(icon = Icons.Default.Close) {})
            Spacer(modifier = Modifier.width(8.dp))
            TsInfoText(text = "placeholder")
        }
    }
}