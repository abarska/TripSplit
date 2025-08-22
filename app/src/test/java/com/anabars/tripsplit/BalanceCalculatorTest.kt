package com.anabars.tripsplit

import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class BalanceCalculatorTest {

    @Test
    fun `calculateDeltasForExpense distributes shares correctly`() {

        val participants = setOf(
            TripParticipant(id = 1, tripId = 100, name = "Alice", multiplicator = 1),
            TripParticipant(id = 2, tripId = 100, name = "Bob", multiplicator = 1)
        )
        val expense = TripExpense(
            id = 1,
            tripId = 100,
            amount = 100.0,
            currency = "USD",
            paidById = 1
        )
        val exchangeRate = 1.0

        val deltas = BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)
        val alice = deltas.find { it.participantId == 1L }!!.deltaUsd
        val bob = deltas.find { it.participantId == 2L }!!.deltaUsd

        assertThat(alice).isEqualTo(BigDecimal("50.00")) // paid 100, owes 50 → net +50
        assertThat(bob).isEqualTo(BigDecimal("-50.00")) // owes 50
    }

    @Test
    fun `calculateDeltasForExpense respects multiplicators`() {

        // given: Alice counts double, Bob counts single
        val participants = setOf(
            TripParticipant(id = 1, tripId = 100, name = "Alice", multiplicator = 2),
            TripParticipant(id = 2, tripId = 100, name = "Bob", multiplicator = 1)
        )
        val expense = TripExpense(
            id = 2,
            tripId = 100,
            amount = 90.0,
            currency = "USD",
            paidById = 1
        )
        val exchangeRate = 1.0

        val deltas = BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)

        // total split factor = 3, each share = 30
        val alice = deltas.find { it.participantId == 1L }!!.deltaUsd
        val bob = deltas.find { it.participantId == 2L }!!.deltaUsd

        assertThat(alice).isEqualTo(BigDecimal("30.00")) // paid 90, owes 60 → net +30
        assertThat(bob).isEqualTo(BigDecimal("-30.00")) // owes 30
    }

    @Test
    fun `calculateDeltasForPayment transfers balance correctly`() {
        val fromUserId = 1L
        val toUserId = 2L
        val payment = TripPayment(
            id = 1,
            tripId = 200,
            amount = 40.0,
            fromUserId = fromUserId,
            toUserId = toUserId,
            currency = "USD"
        )
        val exchangeRate = 1.0

        val deltas = BalanceCalculator.calculateDeltasForPayment(payment, exchangeRate, fromUserId, toUserId)
        val fromUserDelta = deltas.find { it.participantId == fromUserId }!!.deltaUsd
        val toUserDelta = deltas.find { it.participantId == toUserId }!!.deltaUsd

        assertThat(fromUserDelta).isEqualTo(BigDecimal("40.00"))
        assertThat(toUserDelta).isEqualTo(BigDecimal("-40.00"))
    }

    @Test
    fun `calculateDeltasForExpense applies exchange rate correctly`() {
        val participants = setOf(
            TripParticipant(id = 1, tripId = 100, name = "Alice", multiplicator = 1),
            TripParticipant(id = 2, tripId = 100, name = "Bob", multiplicator = 1)
        )
        val expense = TripExpense(
            id = 3,
            tripId = 100,
            amount = 100.0,
            currency = "RON",  // paid in local currency
            paidById = 1
        )
        val exchangeRate = 5.0 // 5 RON = 1 USD

        val deltas = BalanceCalculator.calculateDeltasForExpense(expense, exchangeRate, participants)
        val alice = deltas.find { it.participantId == 1L }!!.deltaUsd
        val bob = deltas.find { it.participantId == 2L }!!.deltaUsd

        assertThat(alice).isEqualTo(BigDecimal("10.00")) // 100/5=20 USD → owes 10, net +10
        assertThat(bob).isEqualTo(BigDecimal("-10.00"))
    }
}