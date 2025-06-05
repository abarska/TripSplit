package com.anabars.tripsplit.ui.itemrows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun ParticipantItemRow(
    name: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttons: List<ActionButton>,
) {
    TripSplitItemRow(modifier = modifier, enabled = enabled) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoText(text = name)
            if (buttons.isNotEmpty()) {
                Row {
                    buttons.forEach { button ->
                        ItemRowActionButton(button)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParticipantItemRowPreviewOneButton() {
    ParticipantItemRow(
        name = "placeholder",
        buttons = listOf(ActionButton(icon = Icons.Default.Stop) {})
    )
}

@Preview(showBackground = true)
@Composable
private fun ParticipantRowPreviewTwoButtons() {
    ParticipantItemRow(
        name = "placeholder",
        buttons = listOf(
            ActionButton(icon = Icons.Default.Pause) {},
            ActionButton(icon = Icons.Default.Stop) {})
    )
}