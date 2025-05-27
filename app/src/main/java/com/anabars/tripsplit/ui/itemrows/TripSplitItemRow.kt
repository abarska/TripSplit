package com.anabars.tripsplit.ui.itemrows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TripSplitItemRow(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .then(Modifier.inputWidthModifier())
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .heightIn(dimensionResource(R.dimen.item_row_height)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripSplitItemRowPreview() {
    TripSplitItemRow(onItemClick = {}) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoText(text = "placeholder")
            val buttons = listOf(
                ActionButton(icon = Icons.Default.Pause) {},
                ActionButton(icon = Icons.Default.Stop) {})
            Row {
                buttons.forEach { button ->
                    ItemRowActionButton(button)
                }
            }
        }
    }
}