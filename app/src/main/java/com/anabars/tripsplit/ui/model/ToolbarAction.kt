package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class ToolbarAction(
    val icon: ImageVector,
    @StringRes val contentDescriptionRes: Int,
    val onClick: () -> Unit
)
