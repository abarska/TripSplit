package com.anabars.tripsplit.ui.widgets

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.TabItem

@Composable
fun TsBottomTabs(selectedTabItem: TabItem, onTabSelected: (TabItem) -> Unit) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TabItem.allTabs().forEach { tab ->
            val isSelected = tab == selectedTabItem
            val contentColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.contentDescriptionRes)
                    )
                },
                label = {
                    TsInfoText(textRes = tab.titleRes, textColor = contentColor)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = contentColor,
                    unselectedIconColor = contentColor,
                    selectedTextColor = contentColor,
                    unselectedTextColor = contentColor,
                    indicatorColor = Color.Transparent
                )
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