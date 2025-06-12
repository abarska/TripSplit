package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants

@Entity(
    tableName = TripSplitConstants.TRIP_CURRENCIES_TABLE,
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
data class TripCurrency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val code: String = "",
    @ColumnInfo val tripId: Long
)
