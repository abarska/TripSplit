package com.anabars.tripsplit.data.room

import androidx.room.TypeConverter
import com.anabars.tripsplit.model.ExpenseCategory
import java.util.Date

object DateConverter {
    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)
}

object ExpenseCategoryConverter{
    @TypeConverter
    fun expenseCategoryToString(category: ExpenseCategory): String {
        return category.name
    }
    @TypeConverter
    fun stringToExpenseCategory(name: String): ExpenseCategory {
        return ExpenseCategory.valueOf(name)
    }
}