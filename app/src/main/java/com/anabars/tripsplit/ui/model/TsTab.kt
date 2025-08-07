package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class TsTab(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val contentDescriptionRes: Int,
    val actions: List<ActionButton.ToolbarActionButton> = emptyList()
)