package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.anabars.tripsplit.R

@Composable
fun <T> TripSplitRadioGroup(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    layout: LayoutType = LayoutType.Row,
    itemWidth: Dp? = null,
    itemContent: @Composable (item: T) -> Unit
) {
    val arrangement = Arrangement.spacedBy(
        dimensionResource(R.dimen.vertical_spacer_small),
        Alignment.CenterHorizontally
    )

    when (layout) {
        LayoutType.Row -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = arrangement
        ) {
            items.forEach { item ->
                TripSplitRadioButton(
                    modifier = itemWidth?.let { Modifier.width(it) } ?: Modifier,
                    value = item,
                    isSelected = item == selectedItem,
                    onItemClick = { onItemSelected(item) },
                    content = { itemContent(item) }
                )
            }
        }

        LayoutType.Flow -> FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = arrangement
        ) {
            items.forEach { item ->
                TripSplitRadioButton(
                    value = item,
                    isSelected = item == selectedItem,
                    onItemClick = { onItemSelected(item) },
                    content = { itemContent(item) }
                )
            }
        }
    }
}

enum class LayoutType { Row, Flow }