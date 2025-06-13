package com.anabars.tripsplit.ui.screens.addtrip

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText
import com.anabars.tripsplit.ui.components.ItemRowActionButton
import com.anabars.tripsplit.ui.components.SecondaryButton
import com.anabars.tripsplit.ui.listitems.TripSplitItemRow
import com.anabars.tripsplit.ui.model.ActionButton

@Composable
fun ChipsSection(
    @StringRes labelRes: Int,
    @StringRes addButtonRes: Int,
    items: List<String>,
    onAddItemButtonClick: () -> Unit,
    onDeleteItemButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoText(textRes = labelRes)

        if (items.isNotEmpty()) {
            Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_small)))
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
                    TripSplitItemRow {
                        Row(modifier = Modifier.padding(8.dp)) {
                            if (items.first() != value) {
                                ItemRowActionButton(button)
                                Spacer(Modifier.width(dimensionResource(R.dimen.vertical_spacer_small)))
                            }
                            InfoText(text = value)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_spacer_normal)))

        SecondaryButton(textRes = addButtonRes) { onAddItemButtonClick() }
    }
}