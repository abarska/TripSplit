package com.anabars.tripsplit.ui.itemrows

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun CurrencyItemRow(code: String, onDeleteCurrency: (String) -> Unit) {
    val button = ActionButton(
        icon = Icons.Default.Close,
        contentDescriptionRes = R.string.delete_item,
    ) { onDeleteCurrency(code) }
    TripSplitItemRow {
        Row(modifier = Modifier.padding(8.dp)) {
            ItemRowActionButton(button)
            Spacer(Modifier.width(dimensionResource(R.dimen.vertical_spacer_small)))
            InfoText(text = code)
        }
    }
}

@Composable
fun ShowParticipant(
    name: String,
    participants: List<String>,
    onDeleteParticipant: (String) -> Unit
) {
    TripSplitItemRow {
        Row(modifier = Modifier.padding(8.dp)) {
            if (name != participants.first()) {
                ItemRowActionButton(
                    ActionButton(
                        icon = Icons.Default.Close,
                        contentDescriptionRes = R.string.delete_item
                    ) { onDeleteParticipant(name) })
                Spacer(Modifier.width(8.dp))
            }
            InfoText(text = name)
        }
    }
}