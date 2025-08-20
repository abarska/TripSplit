package com.anabars.tripsplit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun <T> TsCheckboxPill(
    value: T,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit,
    content: @Composable () -> Unit
) {
    TsRadioButton(
        modifier = modifier,
        value = value,
        onItemClick = { onItemClick(value) },
        isSelected = isSelected
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.vertical_spacer_small),
                Alignment.CenterHorizontally
            )
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                modifier = Modifier.size(20.dp)
            )
            content()
        }
    }
}