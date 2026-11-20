package com.anabars.tripsplit.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class BalanceDaoTest {

    private lateinit var db: TripSplitDatabase
    private var tripId = 0L
    private var a = 0L
    private var b = 0L
    private var c = 0L

    @Before
    fun setUp() = runBlocking<Unit> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripSplitDatabase::class.java).build()
        tripId = db.tripDao().insertTrip(Trip(title = "Lisbon", defaultCurrency = "EUR"))
        db.tripParticipantDao().insertParticipants(
            listOf("A", "B", "C").map { TripParticipant(name = it, tripId = tripId) }
        )
        val ids = db.tripParticipantDao().getParticipantsByTripIdAsList(tripId).map { it.id }
        a = ids[0]; b = ids[1]; c = ids[2]
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun balances() =
        db.balanceDao().getBalances(tripId).first().associate { it.participantId to it.balanceMinor }

    private suspend fun addExpense(
        paidBy: Long, amountMinor: Long, currency: String, rate: BigDecimal,
        tripMinor: Long, shares: Map<Long, Long>
    ): Long {
        val id = db.tripExpensesDao().saveExpense(
            TripExpense(
                paidById = paidBy, amountMinor = amountMinor, currency = currency,
                rateToTrip = rate, tripAmountMinor = tripMinor, tripId = tripId
            )
        )
        db.tripExpensesDao().saveShares(shares.map { (p, s) -> ExpenseShare(id, p, 1, s) })
        return id
    }

    @Test
    fun deleteAfterRateAndWeightChangesLeavesOnlyTheOtherExpense() = runBlocking<Unit> {
        val expense1 = addExpense(
            a, 10000, "USD", BigDecimal("0.9137"), 9137, mapOf(a to 1828, b to 5482, c to 1827)
        )
        addExpense(c, 3000, "EUR", BigDecimal.ONE, 3000, mapOf(a to 1500, c to 1500))
        // B's multiplicator changes afterwards; frozen shares must be unaffected
        db.openHelper.writableDatabase.execSQL(
            "UPDATE trip_participants_table SET multiplicator = 3 WHERE id = $b"
        )

        db.tripExpensesDao().deleteExpenseById(expense1)

        val result = balances()
        assertThat(result).containsExactly(a, -1500L, b, 0L, c, 1500L)
        assertThat(result.values.sum()).isEqualTo(0L)
    }

    @Test
    fun paymentSettlesDebt() = runBlocking<Unit> {
        addExpense(a, 4000, "EUR", BigDecimal.ONE, 4000, mapOf(a to 2000, b to 2000))
        assertThat(balances()[b]).isEqualTo(-2000L)

        db.tripPaymentDao().savePayment(
            TripPayment(
                tripId = tripId, fromUserId = b, toUserId = a, amountMinor = 2000,
                currency = "EUR", rateToTrip = BigDecimal.ONE, tripAmountMinor = 2000
            )
        )

        assertThat(balances()).containsExactly(a, 0L, b, 0L, c, 0L)
    }

    @Test
    fun removedParticipantStillAppearsWithBalance() = runBlocking<Unit> {
        addExpense(a, 3000, "EUR", BigDecimal.ONE, 3000, mapOf(a to 1000, b to 1000, c to 1000))
        db.openHelper.writableDatabase.execSQL(
            "UPDATE trip_participants_table SET status = 'INACTIVE' WHERE id = $b"
        )

        val rows = db.balanceDao().getBalances(tripId).first()

        assertThat(rows).hasSize(3)
        val removed = rows.single { it.participantId == b }
        assertThat(removed.status).isEqualTo(ParticipantStatus.INACTIVE)
        assertThat(removed.balanceMinor).isEqualTo(-1000L)
    }
}
