package com.anabars.tripsplit.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> TsRadioButton(
    value: T,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit,
    content: @Composable () -> Unit
) {
    TsContentCard(
        modifier = modifier,
        isHighlighted = isSelected,
        onItemClick = { onItemClick(value) },
    ) {
        content()
    }
}