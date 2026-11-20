package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.ui.model.AmountCurrencyState
import com.anabars.tripsplit.ui.model.PayerParticipantsState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import java.math.BigDecimal

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
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("tripId"), Index("paidById")]
)
data class TripExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val paidById: Long,
    @ColumnInfo val amountMinor: Long,
    @ColumnInfo val currency: String,
    @ColumnInfo val rateToTrip: BigDecimal,
    @ColumnInfo val tripAmountMinor: Long,
    @ColumnInfo val category: ExpenseCategory = ExpenseCategory.Miscellaneous,
    @ColumnInfo val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo val tripId: Long
)
