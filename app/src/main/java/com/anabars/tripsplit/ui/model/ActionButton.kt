package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R

data class ActionButton(
    val icon: ImageVector,
    val iconSize: Dp = 24.dp,
    @StringRes val contentDescriptionRes: Int = R.string.action_button,
    val onClick: () -> Unit
)