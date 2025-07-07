package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.ui.model.AddExpenseDateCategoryState
import com.anabars.tripsplit.ui.model.AddExpenseUiState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import java.time.ZoneId

@Entity(
    tableName = TripSplitConstants.TRIP_EXPENSES_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripParticipant::class,
            parentColumns = ["id"],
            childColumns = ["paidById"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId", "paidById")]
)
data class TripExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val paidById: Long,
    @ColumnInfo val amount: Double = 0.0,
    @ColumnInfo val currency: String,
    @ColumnInfo val category: ExpenseCategory = ExpenseCategory.Miscellaneous,
    @ColumnInfo val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo val tripId: Long
) {
    companion object {
        fun fromUiState(
            uiState: AddExpenseUiState,
            dateCategoryState: AddExpenseDateCategoryState,
            tripId: Long
        ): TripExpense {
            val timestamp = dateCategoryState.selectedDate
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return TripExpense(
                paidById = uiState.expensePayerId,
                amount = uiState.expenseAmount.toDoubleOrNull() ?: 0.0,
                currency = uiState.expenseCurrencyCode,
                category = dateCategoryState.selectedCategory,
                timestamp = timestamp,
                tripId = tripId
            )
        }
    }
}