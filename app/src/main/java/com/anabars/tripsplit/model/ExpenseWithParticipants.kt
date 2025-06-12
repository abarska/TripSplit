package com.anabars.tripsplit.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExpenseWithParticipants(
    @Embedded val expense: TripExpense,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExpenseParticipantCrossRef::class,
            parentColumn = "expenseId",
            entityColumn = "participantId"
        )
    )
    val participants: List<TripParticipant>
)
