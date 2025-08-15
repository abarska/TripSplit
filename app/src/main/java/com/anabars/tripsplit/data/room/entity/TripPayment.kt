package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.ui.model.AmountCurrencyState
import com.anabars.tripsplit.ui.model.PayerParticipantsState
import java.time.ZoneId

@Entity(
    tableName = TripSplitConstants.TRIP_PAYMENTS_TABLE,
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
            childColumns = ["fromUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripParticipant::class,
            parentColumns = ["id"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId", "fromUserId", "toUserId")]
)
data class TripPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val tripId: Long,
    @ColumnInfo val fromUserId: Long,
    @ColumnInfo val toUserId: Long,
    @ColumnInfo val amount: Double = 0.0,
    @ColumnInfo val currency: String,
    @ColumnInfo val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromUiState(
            amountCurrencyState: AmountCurrencyState,
            payerParticipantsState: PayerParticipantsState,
            tripId: Long
        ): TripPayment {
            val timestamp = amountCurrencyState.selectedDate
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            return TripPayment(
                tripId = tripId,
                fromUserId = payerParticipantsState.expensePayerId!!, // non null after validation
                toUserId = payerParticipantsState.selectedParticipants.first().id,
                amount = amountCurrencyState.expenseAmount.toDoubleOrNull() ?: 0.0,
                currency = amountCurrencyState.expenseCurrencyCode,
                timestamp = timestamp
            )
        }
    }
}