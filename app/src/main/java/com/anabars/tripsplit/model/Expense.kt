package com.anabars.tripsplit.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants

@Entity(
    tableName = TripSplitConstants.TRIP_EXPENSES_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class Expense(
    @ColumnInfo val id: String = "",
    @ColumnInfo val paidBy: String = "",
    @ColumnInfo val sharedWith: List<String> = emptyList(),
    @ColumnInfo val amount: Double = 0.0,
    @ColumnInfo val currency: String,
    @ColumnInfo val category: ExpenseCategory = ExpenseCategory.OTHER,
    @ColumnInfo val note: String = "",
    @ColumnInfo val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo val tripId: Long
)

enum class ExpenseCategory(val icon: ImageVector, @StringRes val titleRes: Int) {
    TRANSPORTATION(Icons.Outlined.DirectionsCar, R.string.expense_category_transportation),
    ACCOMMODATION(Icons.Outlined.Hotel, R.string.expense_category_accommodation),
    FOOD_DRINKS(Icons.Outlined.Restaurant, R.string.expense_category_food_and_drinks),
    ACTIVITIES(Icons.Outlined.Event, R.string.expense_category_activities),
    SHOPPING(Icons.Outlined.ShoppingBag, R.string.expense_category_shopping),
    COMMUNICATION(Icons.Outlined.PhoneAndroid, R.string.expense_category_communication),
    SERVICE_CHARGES(Icons.Outlined.RoomService, R.string.expense_category_service_charges),
    OTHER(Icons.Outlined.MoreHoriz, R.string.expense_category_other)
}