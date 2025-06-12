package com.anabars.tripsplit.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.anabars.tripsplit.common.TripSplitConstants.CROSS_TABLE_EXPENSE_PARTICIPANT

@Entity(
    tableName = CROSS_TABLE_EXPENSE_PARTICIPANT,
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
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseParticipantCrossRef(
    val expenseId: Long,
    val participantId: Long
)
