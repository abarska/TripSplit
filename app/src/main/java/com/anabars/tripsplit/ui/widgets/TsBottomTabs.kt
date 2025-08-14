package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.TabItem
import com.anabars.tripsplit.utils.isLandscapeSmallOrAnyBig

@Composable
fun TsBottomTabs(selectedTabItem: TabItem, onTabSelected: (TabItem) -> Unit) {
    val insets = WindowInsets.navigationBars.asPaddingValues()
    TabRow(
        selectedTabIndex = selectedTabItem.ordinal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = insets.calculateBottomPadding())
    ) {
        val enoughSpace = isLandscapeSmallOrAnyBig(LocalConfiguration.current)
        TabItem.allTabs().forEach { tab: TabItem ->
            val isSelected = tab == selectedTabItem
            val contentColor =
                LocalContentColor.current.copy(alpha = if (isSelected) 1.0f else 0.6f)
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                text = {
                    if (enoughSpace) TsInfoText(textRes = tab.titleRes, textColor = contentColor)
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.contentDescriptionRes),
                        tint = contentColor,
                        modifier = Modifier.size(if (enoughSpace) 24.dp else 32.dp)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TsBottomTabsPreview() {
    TsBottomTabs(
        selectedTabItem = TabItem.Expenses,
        onTabSelected = { }
    )
}