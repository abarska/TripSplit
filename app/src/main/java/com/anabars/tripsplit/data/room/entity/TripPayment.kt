package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import java.math.BigDecimal

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
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TripParticipant::class,
            parentColumns = ["id"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("tripId"), Index("fromUserId"), Index("toUserId")]
)
data class TripPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val tripId: Long,
    @ColumnInfo val fromUserId: Long,
    @ColumnInfo val toUserId: Long,
    @ColumnInfo val amountMinor: Long,
    @ColumnInfo val currency: String,
    @ColumnInfo val rateToTrip: BigDecimal,
    @ColumnInfo val tripAmountMinor: Long,
    @ColumnInfo val timestamp: Long = System.currentTimeMillis()
)
