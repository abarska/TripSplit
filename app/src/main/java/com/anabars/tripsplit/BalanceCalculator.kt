package com.anabars.tripsplit

import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.ui.model.BalanceDelta
import com.anabars.tripsplit.utils.formatters.formatAsCurrency
import java.math.BigDecimal

object BalanceCalculator {

    fun calculateDeltasForExpense(
        expense: TripExpense,
        exchangeRate: Double,
        participants: Set<TripParticipant>
    ): List<BalanceDelta> {
        val deltas = mutableListOf<BalanceDelta>()

        // 1. Add the whole sum paid to the payer's current balance
        deltas.add(delta(expense.tripId, expense.paidById, expense.amount, exchangeRate))

        // 2. Subtract each participant's share from his/her current balance
        val splitFactor = participants.sumOf { it.multiplicator }
        val singleShare = expense.amount / splitFactor
        val participantDeltas = participants.map { participant ->
            delta(
                tripId = expense.tripId,
                participantId = participant.id,
                amount = -1 * singleShare * participant.multiplicator,
                exchangeRate = exchangeRate
            )
        }
        deltas.addAll(participantDeltas)

        return deltas
            .groupBy { it.participantId }
            .map { (id, entries) ->
                BalanceDelta(
                    tripId = expense.tripId,
                    participantId = id,
                    deltaUsd = entries.sumOf { it.deltaUsd }
                )
            }

    }

    fun calculateDeltasForPayment(
        payment: TripPayment,
        exchangeRate: Double,
        fromUserId: Long,
        toUserId: Long
    ): List<BalanceDelta> = listOf(
        // 1. Add the whole sum to the payer's current balance
        delta(payment.tripId, fromUserId, payment.amount, exchangeRate),
        // 2. Subtract the whole sum from the payee's current balance
        delta(payment.tripId, toUserId, -1 * payment.amount, exchangeRate)
    )

    private fun toUsd(amount: Double, exchangeRate: Double): BigDecimal =
        (amount / exchangeRate).toBigDecimal().formatAsCurrency()

    private fun delta(
        tripId: Long,
        participantId: Long,
        amount: Double,
        exchangeRate: Double
    ): BalanceDelta =
        BalanceDelta(
            tripId = tripId,
            participantId = participantId,
            deltaUsd = toUsd(amount, exchangeRate)
        )
}
