package com.anabars.tripsplit.data.room.converters

import androidx.room.TypeConverter
import com.anabars.tripsplit.data.room.entity.ExpenseCategory

object ExpenseCategoryConverter {

    @TypeConverter
    fun expenseCategoryToString(category: ExpenseCategory): String {
        return category.name
    }
    @TypeConverter
    fun stringToExpenseCategory(name: String): ExpenseCategory {
        return ExpenseCategory.valueOf(name)
    }

}