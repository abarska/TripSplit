package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.anabars.tripsplit.R

data class TsTab(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val contentDescriptionRes: Int,
    val actions: List<ActionButton.ToolbarActionButton> = emptyList()
)

val TripDetailsTabs = listOf(
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