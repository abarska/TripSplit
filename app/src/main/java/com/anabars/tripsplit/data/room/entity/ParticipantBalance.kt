package com.anabars.tripsplit.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.anabars.tripsplit.common.TripSplitConstants
import java.math.BigDecimal

@Entity(
    tableName = TripSplitConstants.PARTICIPANT_BALANCES_TABLE,
    primaryKeys = ["tripId", "participantId"]
)
data class ParticipantBalance(
    @ColumnInfo val tripId: Long,
    @ColumnInfo val participantId: Long,
    @ColumnInfo val amountUsd: BigDecimal = BigDecimal.ZERO,
)