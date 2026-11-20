package com.anabars.tripsplit.data.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant

data class ExpenseWithParticipants(
    @Embedded val expense: TripExpense,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExpenseShare::class,
            parentColumn = "expenseId",
            entityColumn = "participantId"
        )
    )
    val participants: List<TripParticipant>
)
