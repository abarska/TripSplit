package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.ui.graphics.vector.ImageVector
import com.anabars.tripsplit.R

sealed class TabItem(
    val ordinal: Int,
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    @StringRes val contentDescriptionRes: Int
) {
    data object Expenses : TabItem(
        ordinal = 0,
        icon = Icons.Outlined.AttachMoney,
        titleRes = R.string.expenses_tab,
        contentDescriptionRes = R.string.expenses_tab_content_description
    )

    data object Payments : TabItem(
        ordinal = 1,
        icon = Icons.AutoMirrored.Outlined.CompareArrows,
        titleRes = R.string.payments_tab,
        contentDescriptionRes = R.string.payments_tab_content_description
    )

    data object Balances : TabItem(
        ordinal = 2,
        icon = Icons.Outlined.Balance,
        titleRes = R.string.balances_tab,
        contentDescriptionRes = R.string.balances_tab_content_description
    )

    companion object {
        fun allTabs() = listOf(Expenses, Payments, Balances)
    }
}