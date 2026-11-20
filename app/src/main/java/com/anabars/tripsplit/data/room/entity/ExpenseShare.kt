package com.anabars.tripsplit.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.anabars.tripsplit.common.TripSplitConstants.EXPENSE_SHARES_TABLE

@Entity(
    tableName = EXPENSE_SHARES_TABLE,
    primaryKeys = ["expenseId", "participantId"],
    foreignKeys = [
        ForeignKey(
            entity = TripExpense::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripParticipant::class,
            parentColumns = ["id"],
            childColumns = ["participantId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("participantId")]
)
data class ExpenseShare(
    val expenseId: Long,
    val participantId: Long,
    val weight: Int,
    val shareMinor: Long
)
