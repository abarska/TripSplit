package com.anabars.tripsplit.ui.itemrows

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TripItemRow(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onItemClick: () -> Unit = {}
) {
    TripSplitItemRow(
        modifier = modifier.inputWidthModifier(),
        enabled = enabled,
        onItemClick = onItemClick
    ) {
        InfoText(modifier = Modifier.padding(8.dp), text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun TripItemRowPreview() {
    TripItemRow(text = "Placeholder") {}
}