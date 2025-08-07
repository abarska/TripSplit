package com.anabars.tripsplit.ui.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.TsInfoText
import com.anabars.tripsplit.ui.model.TsTab

@Composable
fun TsBottomTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val insets = WindowInsets.navigationBars.asPaddingValues()
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = insets.calculateBottomPadding())
    ) {
        tripDetailsTabs().forEachIndexed { index, tab ->
            val isSelected = selectedTabIndex == index
            val contentColor =
                LocalContentColor.current.copy(alpha = if (isSelected) 1.0f else 0.6f)
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    TsInfoText(textRes = tab.titleRes, textColor = contentColor)
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.contentDescriptionRes),
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    }
}

fun tripDetailsTabs() = listOf(
    TsTab(
        icon = Icons.Outlined.Home,
        titleRes = R.string.overview_tab,
        contentDescriptionRes = R.string.overview_tab_content_description
    ),
    TsTab(
        icon = Icons.Outlined.AttachMoney,
        titleRes = R.string.expenses_tab,
        contentDescriptionRes = R.string.expenses_tab_content_description
    ),
    TsTab(
        icon = Icons.AutoMirrored.Outlined.CompareArrows,
        titleRes = R.string.payments_tab,
        contentDescriptionRes = R.string.payments_tab_content_description
    )
)

@Preview(showBackground = true)
@Composable
private fun TsBottomTabsPreview() {
    TsBottomTabs(
        selectedTabIndex = 0,
        onTabSelected = { }
    )
}