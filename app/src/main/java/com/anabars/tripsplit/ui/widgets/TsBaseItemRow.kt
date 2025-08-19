package com.anabars.tripsplit.ui.widgets

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.utils.TsFontSize
import com.anabars.tripsplit.ui.utils.inputWidthModifier

@Composable
fun TsBaseItemRow(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onDeleteClick: () -> Unit = {},
    visiblePart: @Composable ColumnScope.() -> Unit
) {
    TsContentCard(modifier = modifier.inputWidthModifier()) {
        Column(modifier = modifier.fillMaxWidth()) {
            visiblePart()
            AnimatedVisibility(visible = isExpanded) {
                TsBaseHiddenPart(labelRes = R.string.delete, onClick = onDeleteClick)
            }
        }
    }
}

@Composable
fun TsBaseVisiblePart(
    icon: ImageVector,
    iconContentDescription: String,
    isExpanded: Boolean,
    amountText: String,
    action: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.vertical_spacer_normal))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier.size(36.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            content()
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            TsInfoText(
                text = amountText,
                fontSize = TsFontSize.MEDIUM
            )
            Spacer(modifier = Modifier.height(16.dp))
            TsExpandCollapseToggle(isExpanded = isExpanded, action = action)
        }
    }
}

@Composable
fun TsBaseHiddenPart(@StringRes labelRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            TsInfoText(
                textRes = labelRes,
                fontSize = TsFontSize.MEDIUM,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}