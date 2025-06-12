package com.anabars.tripsplit.data.room.converters

import androidx.room.TypeConverter
import com.anabars.tripsplit.ui.model.ExpenseCategory

object ExpenseCategoryConverter {

    @TypeConverter
    fun fromCategory(category: ExpenseCategory): String {
        return category.javaClass.simpleName ?: ExpenseCategory.Miscellaneous.javaClass.simpleName
    }

    @TypeConverter
    fun toCategory(name: String): ExpenseCategory {
        return ExpenseCategory.allExpenseCategories().find { it.javaClass.simpleName == name }
            ?: ExpenseCategory.Miscellaneous
    }

}