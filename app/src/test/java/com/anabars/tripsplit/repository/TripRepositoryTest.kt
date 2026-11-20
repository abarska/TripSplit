package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripPayment
import java.math.BigDecimal
import org.mockito.kotlin.never
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.coroutines.Continuation

class TripRepositoryTest {

    private val db: TripSplitDatabase = mock()
    private val tripDao: TripDao = mock()
    private val tripExpensesDao: TripExpensesDao = mock()
    private val tripPaymentDao: TripPaymentDao = mock()
    private val participantDao: TripParticipantDao = mock()
    private val exchangeRateDao: ExchangeRateDao = mock()
    private val currencyDao: TripCurrencyDao = mock()

    private lateinit var repository: TripRepository
    private lateinit var roomStatic: MockedStatic<*>

    private var transactionsStarted = 0
    private var insideTransaction = false
    private val writesOutsideTransaction = mutableListOf<String>()

    @Before
    fun setup() {
        // Room's withTransaction needs a real database; run its block inline and record that it ran
        roomStatic = mockStatic(Class.forName("androidx.room.RoomDatabaseKt")) { invocation ->
            if (invocation.method.name != "withTransaction") return@mockStatic null
            @Suppress("UNCHECKED_CAST")
            val block = invocation.rawArguments[1] as (Continuation<Any?>) -> Any?
            transactionsStarted++
            insideTransaction = true
            try {
                block(invocation.rawArguments[2] as Continuation<Any?>)
            } finally {
                insideTransaction = false
            }
        }
        repository = TripRepository(
            db, tripDao, tripExpensesDao, tripPaymentDao, participantDao, exchangeRateDao, currencyDao
        )
    }

    @After
    fun tearDown() {
        roomStatic.close()
    }

    private fun recordWriteOutsideTransaction(name: String) {
        if (!insideTransaction) writesOutsideTransaction += name
    }

    @Test
    fun `saveTrip persists the default currency of a new trip`() = runTest {
        runBlocking { whenever(tripDao.insertTrip(any())).thenReturn(7L) }
        runBlocking { whenever(participantDao.getParticipantsByTripIdAsList(7L)).thenReturn(emptyList()) }
        runBlocking { whenever(currencyDao.getCurrenciesByTripIdAsList(7L)).thenReturn(emptyList()) }

        repository.saveTrip(
            tripId = null,
            trip = Trip(title = "Rome", defaultCurrency = "EUR"),
            participants = listOf(TripParticipant(name = "A", tripId = 0L)),
            currencyCodes = listOf("EUR", "USD")
        )

        val saved = argumentCaptor<Trip>()
        verify(tripDao).insertTrip(saved.capture())
        assertThat(saved.firstValue.defaultCurrency).isEqualTo("EUR")
    }

    @Test
    fun `saveTrip persists the default currency of an edited trip`() = runTest {
        runBlocking { whenever(participantDao.getParticipantsByTripIdAsList(7L)).thenReturn(emptyList()) }
        runBlocking { whenever(currencyDao.getCurrenciesByTripIdAsList(7L)).thenReturn(emptyList()) }
        stubStoredTrip(currency = "EUR")

        repository.saveTrip(
            tripId = 7L,
            trip = Trip(title = "Rome", defaultCurrency = "GBP"),
            participants = emptyList(),
            currencyCodes = listOf("GBP")
        )

        val saved = argumentCaptor<Trip>()
        verify(tripDao).updateTrip(saved.capture())
        assertThat(saved.firstValue.id).isEqualTo(7L)
        assertThat(saved.firstValue.defaultCurrency).isEqualTo("GBP")
    }

    @Test
    fun `saveTrip runs every write in one transaction`() = runTest {
        runBlocking {
            whenever(tripDao.insertTrip(any())).thenAnswer {
                recordWriteOutsideTransaction("trip"); 7L
            }
            whenever(participantDao.getParticipantsByTripIdAsList(7L)).thenReturn(emptyList())
            whenever(currencyDao.getCurrenciesByTripIdAsList(7L)).thenReturn(emptyList())
            whenever(participantDao.upsertParticipants(any())).thenAnswer {
                recordWriteOutsideTransaction("participants")
            }
            whenever(currencyDao.upsertCurrencies(any())).thenAnswer {
                recordWriteOutsideTransaction("currencies")
            }
        }

        repository.saveTrip(
            tripId = null,
            trip = Trip(title = "Rome"),
            participants = listOf(TripParticipant(name = "A", tripId = 0L)),
            currencyCodes = listOf("USD")
        )

        assertThat(transactionsStarted).isEqualTo(1)
        assertThat(writesOutsideTransaction).isEmpty()
    }

    @Test
    fun `saveTrip propagates a failure partway through so the transaction rolls back`() = runTest {
        runBlocking {
            whenever(tripDao.insertTrip(any())).thenReturn(7L)
            whenever(participantDao.getParticipantsByTripIdAsList(7L)).thenReturn(emptyList())
            whenever(participantDao.upsertParticipants(any()))
                .doThrow(IllegalStateException("disk full"))
        }

        val failure = runCatching {
            repository.saveTrip(
                tripId = null,
                trip = Trip(title = "Rome"),
                participants = listOf(TripParticipant(name = "A", tripId = 0L)),
                currencyCodes = listOf("USD")
            )
        }.exceptionOrNull()

        assertThat(failure).isInstanceOf(IllegalStateException::class.java)
        assertThat(transactionsStarted).isEqualTo(1)
        runBlocking { verify(currencyDao, org.mockito.kotlin.never()).upsertCurrencies(any()) }
    }

    private fun stubStoredTrip(
        currency: String,
        expenses: List<TripExpense> = emptyList(),
        shares: Map<Long, List<ExpenseShare>> = emptyMap(),
        payments: List<TripPayment> = emptyList(),
        rates: Map<String, String> = emptyMap()
    ) = runBlocking {
        whenever(tripDao.getTripById(7L)).thenReturn(Trip(id = 7L, defaultCurrency = currency))
        whenever(tripExpensesDao.getExpensesByTripIdAsList(7L)).thenReturn(expenses)
        expenses.forEach {
            whenever(tripExpensesDao.getSharesByExpenseId(it.id)).thenReturn(shares[it.id].orEmpty())
        }
        whenever(tripPaymentDao.getPaymentsByTripIdAsList(7L)).thenReturn(payments)
        whenever(participantDao.getParticipantsByTripIdAsList(7L)).thenReturn(emptyList())
        whenever(currencyDao.getCurrenciesByTripIdAsList(7L)).thenReturn(emptyList())
        whenever(exchangeRateDao.getRateForCurrency(any())).thenAnswer { inv ->
            val code = inv.getArgument<String>(0)
            if (code == "USD") BigDecimal.ONE else rates[code]?.let { BigDecimal(it) }
        }
    }

    private fun expense(id: Long, amountMinor: Long, currency: String, rateToTrip: String, tripMinor: Long) =
        TripExpense(
            id = id, paidById = 1L, amountMinor = amountMinor, currency = currency,
            rateToTrip = BigDecimal(rateToTrip), tripAmountMinor = tripMinor, tripId = 7L
        )

    private suspend fun switchTo(currency: String) = repository.saveTrip(
        tripId = 7L,
        trip = Trip(title = "Rome", defaultCurrency = currency),
        participants = emptyList(),
        currencyCodes = listOf(currency)
    )

    private val twoSharers = listOf(1L, 2L).map { ExpenseShare(0L, it, 1, 0L) }

    @Test
    fun `switch with mixed items converts from the original amounts`() = runTest {
        val expenses = listOf(
            expense(1, 10000, "USD", "0.9137", 9137),
            expense(2, 2000, "GBP", "1.17", 2340),
            expense(3, 30000, "EUR", "1", 30000)
        )
        stubStoredTrip(
            "EUR", expenses,
            shares = expenses.associate { it.id to twoSharers.map { s -> s.copy(expenseId = it.id) } },
            rates = mapOf("EUR" to "1", "GBP" to "0.85")
        )

        val result = switchTo("GBP")

        assertThat(result).isEqualTo(TripItemResult.Success)
        val updated = argumentCaptor<TripExpense>()
        runBlocking { verify(tripExpensesDao, org.mockito.kotlin.times(3)).updateExpense(updated.capture()) }
        assertThat(updated.allValues.map { it.tripAmountMinor }).containsExactly(7766L, 2000L, 25500L).inOrder()
        assertThat(updated.allValues[1].rateToTrip).isEqualTo(BigDecimal.ONE)
    }

    @Test
    fun `switch reallocates shares and keeps them summing to the new total`() = runTest {
        val expenses = listOf(expense(1, 10000, "USD", "0.9137", 9137))
        stubStoredTrip(
            "EUR", expenses,
            shares = mapOf(1L to listOf(ExpenseShare(1, 1, 1, 4569), ExpenseShare(1, 2, 1, 4568))),
            rates = mapOf("EUR" to "1", "GBP" to "0.85")
        )

        switchTo("GBP")

        val saved = argumentCaptor<List<ExpenseShare>>()
        runBlocking { verify(tripExpensesDao).saveShares(saved.capture()) }
        // 7766 split in two, the leftover goes to the payer (participant 1)
        assertThat(saved.firstValue.associate { it.participantId to it.shareMinor })
            .containsExactly(1L, 3883L, 2L, 3883L)
        assertThat(saved.firstValue.sumOf { it.shareMinor }).isEqualTo(7766L)
    }

    @Test
    fun `switching back restores items entered in the trip currency exactly`() = runTest {
        // the EUR item was moved to GBP at 0.85, now the trip returns to EUR at 1 / 0.85
        val expenses = listOf(expense(1, 30000, "EUR", "0.85", 25500))
        stubStoredTrip(
            "GBP", expenses,
            shares = mapOf(1L to twoSharers.map { it.copy(expenseId = 1) }),
            rates = mapOf("EUR" to "1", "GBP" to "0.85")
        )

        switchTo("EUR")

        val updated = argumentCaptor<TripExpense>()
        runBlocking { verify(tripExpensesDao).updateExpense(updated.capture()) }
        assertThat(updated.firstValue.tripAmountMinor).isEqualTo(30000L)
        assertThat(updated.firstValue.rateToTrip).isEqualTo(BigDecimal.ONE)
    }

    @Test
    fun `payments are converted too`() = runTest {
        val payment = TripPayment(
            id = 5, tripId = 7L, fromUserId = 1, toUserId = 2, amountMinor = 10000,
            currency = "USD", rateToTrip = BigDecimal("0.9137"), tripAmountMinor = 9137
        )
        stubStoredTrip("EUR", payments = listOf(payment), rates = mapOf("EUR" to "1", "GBP" to "0.85"))

        switchTo("GBP")

        val updated = argumentCaptor<TripPayment>()
        runBlocking { verify(tripPaymentDao).updatePayment(updated.capture()) }
        assertThat(updated.firstValue.tripAmountMinor).isEqualTo(7766L)
    }

    @Test
    fun `switch without a rate writes nothing and reports the missing rate`() = runTest {
        val expenses = listOf(expense(1, 30000, "EUR", "1", 30000))
        stubStoredTrip("EUR", expenses, rates = mapOf("EUR" to "1"))

        val result = switchTo("GBP")

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        runBlocking {
            verify(tripDao, never()).updateTrip(any())
            verify(tripExpensesDao, never()).updateExpense(any())
            verify(participantDao, never()).upsertParticipants(any())
            verify(currencyDao, never()).upsertCurrencies(any())
        }
    }

    @Test
    fun `switch on an empty trip needs no rates`() = runTest {
        stubStoredTrip("EUR")

        val result = switchTo("GBP")

        assertThat(result).isEqualTo(TripItemResult.Success)
        val saved = argumentCaptor<Trip>()
        runBlocking { verify(tripDao).updateTrip(saved.capture()) }
        assertThat(saved.firstValue.defaultCurrency).isEqualTo("GBP")
    }
}
