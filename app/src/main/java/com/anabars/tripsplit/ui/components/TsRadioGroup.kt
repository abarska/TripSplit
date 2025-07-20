package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun <T> TsRadioGroup(
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
            TsRadioGroupItems(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                itemWidth = itemWidth,
                itemContent = itemContent
            )
        }

        LayoutType.Flow -> FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = arrangement
        ) {
            TsRadioGroupItems(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                itemContent = itemContent
            )
        }

        LayoutType.Column -> Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TsRadioGroupItems(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = onItemSelected,
                itemWidth = itemWidth,
                itemContent = itemContent
            )
        }
    }
}

@Composable
private fun <T> TsRadioGroupItems(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemWidth: Dp? = null,
    itemContent: @Composable (T) -> Unit
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

enum class LayoutType { Row, Flow, Column }