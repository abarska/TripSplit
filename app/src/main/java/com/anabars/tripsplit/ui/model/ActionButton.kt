package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

sealed class ActionButton {
    abstract val icon: ImageVector

    @get:StringRes
    abstract val contentDescriptionRes: Int

    abstract val onClick: () -> Unit

    data class ToolbarActionButton(
        override val icon: ImageVector,
        @StringRes override val contentDescriptionRes: Int = R.string.toolbar_action_button,
        override val onClick: () -> Unit
    ) : ActionButton()

    data class ChipActionButton(
        override val icon: ImageVector,
        @StringRes override val contentDescriptionRes: Int = R.string.chip_action_button,
        val iconSize: Dp = 24.dp,
        val enabled: Boolean = true,
        override val onClick: () -> Unit
    ) : ActionButton()
}