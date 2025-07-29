package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

@Composable
fun TsExpandCollapseToggle(isExpanded: Boolean, modifier: Modifier = Modifier, action: () -> Unit) {
    val contentDescriptionRes =
        if (isExpanded) R.string.collapse_card_icon
        else R.string.expand_card_icon
    Icon(
        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
        contentDescription = stringResource(contentDescriptionRes),
        modifier = modifier
            .size(24.dp)
            .clickable { action() }
    )
}

@Preview(showBackground = true)
@Composable
private fun TsExpandCollapseTogglePreviewCollapsed() {
    TsExpandCollapseToggle(isExpanded = false) {}
}

@Preview(showBackground = true)
@Composable
private fun TsExpandCollapseTogglePreviewExpanded() {
    TsExpandCollapseToggle(isExpanded = true) {}
}