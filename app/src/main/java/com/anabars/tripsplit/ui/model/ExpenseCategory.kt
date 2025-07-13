package com.anabars.tripsplit.ui.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.anabars.tripsplit.R

sealed class ExpenseCategory(val icon: ImageVector, @StringRes val titleRes: Int) {

    companion object {
        fun allExpenseCategories() =
            listOf(Transportation, Accommodation, Food, Activities, Miscellaneous)
    }

    data object Transportation :
        ExpenseCategory(Icons.Outlined.DirectionsCar, R.string.expense_category_transportation)

    data object Accommodation :
        ExpenseCategory(Icons.Outlined.Hotel, R.string.expense_category_accommodation)

    data object Food :
        ExpenseCategory(Icons.Outlined.Restaurant, R.string.expense_category_food)

    data object Activities :
        ExpenseCategory(Icons.Outlined.AccountBalance, R.string.expense_category_activities)

    data object Miscellaneous :
        ExpenseCategory(Icons.Outlined.Category, R.string.expense_category_miscellaneous)
}