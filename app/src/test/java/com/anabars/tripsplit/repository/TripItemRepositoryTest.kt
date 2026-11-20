package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.entity.ExpenseShare
import com.anabars.tripsplit.data.room.entity.Trip
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import kotlin.coroutines.Continuation

@OptIn(ExperimentalCoroutinesApi::class)
class TripItemRepositoryTest {

    private val db: TripSplitDatabase = mock()
    private val tripCurrencyDao: TripCurrencyDao = mock()
    private val tripParticipantDao: TripParticipantDao = mock()
    private val tripExpensesDao: TripExpensesDao = mock()
    private val tripPaymentDao: TripPaymentDao = mock()
    private val tripDao: TripDao = mock()
    private val exchangeRateDao: ExchangeRateDao = mock()

    private lateinit var repository: TripItemRepository

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var roomStatic: MockedStatic<*>

    private fun expense(currency: String, amountMinor: Long = 3000L) = TripExpense(
        id = 1L, paidById = 1L, amountMinor = amountMinor, currency = currency,
        rateToTrip = BigDecimal.ONE, tripAmountMinor = 0L, tripId = 1L
    )

    private fun payment(currency: String, amountMinor: Long = 1000L) = TripPayment(
        id = 1L, tripId = 1L, fromUserId = 1L, toUserId = 2L, amountMinor = amountMinor,
        currency = currency, rateToTrip = BigDecimal.ONE, tripAmountMinor = 0L
    )

    private fun givenTripCurrency(code: String) = runBlocking {
        whenever(tripDao.getTripById(1L)).thenReturn(Trip(id = 1L, defaultCurrency = code))
    }

    private fun givenRate(code: String, rate: String) = runBlocking {
        whenever(exchangeRateDao.getExchangeRateForCurrency(code))
            .thenReturn(ExchangeRate(currencyCode = code, baseCurrency = "USD", rate = BigDecimal(rate)))
    }
    private val participants = setOf(
        TripParticipant(id = 1L, name = "A", tripId = 1L),
        TripParticipant(id = 2L, name = "B", tripId = 1L)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Room's withTransaction needs a real database; run its block inline instead
        roomStatic = mockStatic(Class.forName("androidx.room.RoomDatabaseKt")) { invocation ->
            if (invocation.method.name != "withTransaction") return@mockStatic null
            @Suppress("UNCHECKED_CAST")
            val block = invocation.rawArguments[1] as (Continuation<Any?>) -> Any?
            block(invocation.rawArguments[2] as Continuation<Any?>)
        }
        // run the DAO's base-currency rule for real so it reaches the stubbed lookup
        runBlocking { doCallRealMethod().whenever(exchangeRateDao).getRateForCurrency(any()) }
        repository = TripItemRepository(
            db, tripDao, tripCurrencyDao, tripParticipantDao, tripExpensesDao,
            tripPaymentDao, exchangeRateDao
        )
    }

    @After
    fun tearDown() {
        roomStatic.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `saveExpense in a foreign currency before first sync returns MissingExchangeRate and writes nothing`() = runTest {
        givenTripCurrency("UAH")

        val result = repository.saveExpenseWithParticipants(expense("EUR"), participants)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripExpensesDao, never()).saveExpense(any())
        verify(tripExpensesDao, never()).saveShares(any())
    }

    @Test
    fun `saveExpense is refused when only the trip currency rate is missing`() = runTest {
        givenTripCurrency("UAH")
        givenRate("EUR", "0.9")

        val result = repository.saveExpenseWithParticipants(expense("EUR"), participants)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripExpensesDao, never()).saveExpense(any())
    }

    @Test
    fun `savePayment in a foreign currency before first sync returns MissingExchangeRate and writes nothing`() = runTest {
        givenTripCurrency("UAH")

        val result = repository.savePayment(payment("EUR"))

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripPaymentDao, never()).savePayment(any())
    }

    @Test
    fun `saveExpense in the trip currency succeeds with an empty rate table`() = runTest {
        givenTripCurrency("UAH")
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(1L)

        val result = repository.saveExpenseWithParticipants(expense("UAH"), participants)

        assertThat(result).isEqualTo(TripItemResult.Success)
        val saved = argumentCaptor<TripExpense>().apply { verify(tripExpensesDao).saveExpense(capture()) }.firstValue
        assertThat(saved.rateToTrip).isEqualTo(BigDecimal.ONE)
        assertThat(saved.tripAmountMinor).isEqualTo(3000L)
        verify(exchangeRateDao, never()).getExchangeRateForCurrency(any())
    }

    @Test
    fun `saveExpense in USD succeeds in a USD trip with an empty rate table`() = runTest {
        givenTripCurrency("USD")
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(1L)

        val result = repository.saveExpenseWithParticipants(expense("USD"), participants)

        assertThat(result).isEqualTo(TripItemResult.Success)
    }

    @Test
    fun `saveExpense in a foreign currency freezes the rate and total and allocates exact shares`() = runTest {
        givenTripCurrency("EUR")
        givenRate("EUR", "0.9137")
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(7L)
        val three = participants + TripParticipant(id = 3L, name = "C", tripId = 1L)

        val result = repository.saveExpenseWithParticipants(expense("USD", 10000L), three)

        assertThat(result).isEqualTo(TripItemResult.Success)
        val saved = argumentCaptor<TripExpense>().apply { verify(tripExpensesDao).saveExpense(capture()) }.firstValue
        assertThat(saved.rateToTrip).isEqualTo(BigDecimal("0.9137"))
        assertThat(saved.tripAmountMinor).isEqualTo(9137L)
        val shares = argumentCaptor<List<ExpenseShare>>().apply { verify(tripExpensesDao).saveShares(capture()) }.firstValue
        assertThat(shares.map { it.expenseId }.toSet()).containsExactly(7L)
        assertThat(shares.sumOf { it.shareMinor }).isEqualTo(9137L)
        // payer (id 1) takes the leftover cent: 3045 + 3046... floor(9137/3) = 3045, leftover 2
        assertThat(shares.associate { it.participantId to it.shareMinor })
            .containsExactly(1L, 3047L, 2L, 3045L, 3L, 3045L)
    }

    @Test
    fun `saveExpense snapshots the participants' current multiplicators as weights`() = runTest {
        givenTripCurrency("EUR")
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(1L)
        val weighted = setOf(
            TripParticipant(id = 1L, name = "A", multiplicator = 1, tripId = 1L),
            TripParticipant(id = 2L, name = "B", multiplicator = 3, tripId = 1L)
        )

        repository.saveExpenseWithParticipants(expense("EUR", 4000L), weighted)

        val shares = argumentCaptor<List<ExpenseShare>>().apply { verify(tripExpensesDao).saveShares(capture()) }.firstValue
        assertThat(shares.associate { it.participantId to it.weight }).containsExactly(1L, 1, 2L, 3)
        assertThat(shares.associate { it.participantId to it.shareMinor }).containsExactly(1L, 1000L, 2L, 3000L)
    }

    @Test
    fun `savePayment in a foreign currency freezes the rate and total`() = runTest {
        givenTripCurrency("EUR")
        givenRate("EUR", "0.9137")

        val result = repository.savePayment(payment("USD", 10000L))

        assertThat(result).isEqualTo(TripItemResult.Success)
        val saved = argumentCaptor<TripPayment>().apply { verify(tripPaymentDao).savePayment(capture()) }.firstValue
        assertThat(saved.rateToTrip).isEqualTo(BigDecimal("0.9137"))
        assertThat(saved.tripAmountMinor).isEqualTo(9137L)
    }
}
