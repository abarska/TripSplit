package com.anabars.tripsplit.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class TripDeletionCascadeTest {

    private lateinit var db: TripSplitDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TripSplitDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deletingTripRemovesEveryChildRow() = runBlocking {
        val tripId = db.tripDao().insertTrip(Trip(title = "Lisbon", defaultCurrency = "EUR"))
        db.tripCurrencyDao().insertCurrencies(listOf(TripCurrency(code = "EUR", tripId = tripId)))
        db.tripParticipantDao().insertParticipants(
            listOf(
                TripParticipant(name = "A", tripId = tripId),
                TripParticipant(name = "B", tripId = tripId)
            )
        )
        val (a, b) = db.tripParticipantDao().getParticipantsByTripIdAsList(tripId).map { it.id }

        val expenseId = db.tripExpensesDao().saveExpense(
            TripExpense(
                paidById = a,
                amountMinor = 1000,
                currency = "EUR",
                rateToTrip = BigDecimal.ONE,
                tripAmountMinor = 1000,
                tripId = tripId
            )
        )
        db.tripExpensesDao().saveShares(
            listOf(
                ExpenseShare(expenseId, a, weight = 1, shareMinor = 500),
                ExpenseShare(expenseId, b, weight = 1, shareMinor = 500)
            )
        )
        db.tripPaymentDao().savePayment(
            TripPayment(
                tripId = tripId,
                fromUserId = b,
                toUserId = a,
                amountMinor = 500,
                currency = "EUR",
                rateToTrip = BigDecimal.ONE,
                tripAmountMinor = 500
            )
        )
        assertThat(rowCount(TripSplitConstants.EXPENSE_SHARES_TABLE)).isEqualTo(2)

        db.tripDao().deleteTrip(db.tripDao().getTripById(tripId))

        listOf(
            TripSplitConstants.TRIP_TABLE,
            TripSplitConstants.TRIP_PARTICIPANTS_TABLE,
            TripSplitConstants.TRIP_CURRENCIES_TABLE,
            TripSplitConstants.TRIP_EXPENSES_TABLE,
            TripSplitConstants.EXPENSE_SHARES_TABLE,
            TripSplitConstants.TRIP_PAYMENTS_TABLE
        ).forEach { table ->
            assertThat(rowCount(table)).isEqualTo(0)
        }
    }

    private fun rowCount(table: String): Int =
        db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use {
            it.moveToFirst()
            it.getInt(0)
        }
}
