package com.anabars.tripsplit.ui.itemrows

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.ui.components.InfoText

@Composable
fun TripItemRow(
    text: String,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    TripSplitItemRow(
        modifier = modifier,
        onItemClick = onItemClick
    ) {
        InfoText(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun TripItemRowPreview() {
    TripItemRow(text = "Placeholder") {}
}