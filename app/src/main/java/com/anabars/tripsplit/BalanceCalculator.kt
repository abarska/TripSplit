package com.anabars.tripsplit

import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.model.BalanceDelta
import java.math.RoundingMode

object BalanceCalculator {

    fun calculateDeltas(
        expense: TripExpense,
        participants: Set<TripParticipant>
    ): List<BalanceDelta> {
        val deltas = mutableListOf<BalanceDelta>()

        // 1. Add the whole sum paid to the payer's current balance
        deltas.add(
            BalanceDelta(
                tripId = expense.tripId,
                participantId = expense.paidById,
                deltaUsd = expense.amount.toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP)
            )
        )

        // 2. Subtract each participant's share from their current balance
        val splitFactor = participants.sumOf { it.multiplicator }
        val singleShare = expense.amount / splitFactor

        participants.forEach { participant ->
            val deltaUsd = (singleShare * participant.multiplicator * -1)
                .toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP)

            deltas.add(
                BalanceDelta(
                    tripId = expense.tripId,
                    participantId = participant.id,
                    deltaUsd = deltaUsd
                )
            )
        }

        return deltas
    }
}