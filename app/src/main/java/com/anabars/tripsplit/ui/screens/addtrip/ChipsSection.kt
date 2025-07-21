package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.components.TsItemRowActionButton
import com.anabars.tripsplit.ui.listitems.TsItemRow
import com.anabars.tripsplit.ui.model.ActionButton
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.getFakeTripCurrencies
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun <T> ChipsSection(
    leadingIcon: ImageVector,
    items: List<T>,
    onAddItemButtonClick: () -> Unit,
    onDeleteItemButtonClick: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_small))
        ) {
            items.forEach { value ->
                val button = ActionButton.ChipActionButton(
                    icon = Icons.Default.Close,
                    contentDescriptionRes = R.string.delete_item,
                ) { onDeleteItemButtonClick(value) }
                TsItemRow(onItemClick = { onItemClick(value) }) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (items.first() != value) {
                            TsItemRowActionButton(button)
                        }
                        TsInfoText(text = itemLabel(value), fontSize = TsFontSize.MEDIUM)
                    }
                }
            }
            TsItemRow(highlighted = true, onItemClick = onAddItemButtonClick) {
                val button = ActionButton.ChipActionButton(
                    icon = leadingIcon,
                    contentDescriptionRes = R.string.add_item,
                ){}
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TsItemRowActionButton(button)
                    TsInfoText(textRes = R.string.add, fontSize = TsFontSize.MEDIUM)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChipsSectionPreview() {
    ChipsSection(
        leadingIcon = Icons.Outlined.People,
        items = getFakeTripCurrencies(),
        onAddItemButtonClick = {},
        onDeleteItemButtonClick = {},
        itemLabel = { it.code },
        modifier = Modifier.inputWidthModifier(),
        onItemClick = {}
    )
}