package com.anabars.tripsplit.ui.screens.addtrip

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsItemRowActionButton
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun <T> ChipsSection(
    @StringRes labelRes: Int,
    items: List<T>,
    onAddItemButtonClick: () -> Unit,
    onDeleteItemButtonClick: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_small))
    ) {
        TsInfoText(textRes = labelRes)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_small)),
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { value ->
                val button = ActionButton(
                    icon = Icons.Default.Close,
                    contentDescriptionRes = R.string.delete_item,
                ) { onDeleteItemButtonClick(value) }
                TsItemRow {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (items.first() != value) {
                            TsItemRowActionButton(button)
                        }
                        TsInfoText(text = itemLabel(value))
                    }
                }
            }
            TsItemRow(highlighted = true, onItemClick = onAddItemButtonClick) {
                val button = ActionButton(
                    icon = Icons.Default.Add,
                    contentDescriptionRes = R.string.add_item,
                )
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TsItemRowActionButton(button)
                    TsInfoText(textRes = R.string.add)
                }
            }
        }
    }
}