package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripPayment
import com.anabars.tripsplit.data.room.model.ExpenseWithParticipants
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
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import kotlin.coroutines.Continuation

@OptIn(ExperimentalCoroutinesApi::class)
class TripItemRepositoryTest {

    private val db: TripSplitDatabase = mock()
    private val tripCurrencyDao: TripCurrencyDao = mock()
    private val tripParticipantDao: TripParticipantDao = mock()
    private val tripExpensesDao: TripExpensesDao = mock()
    private val tripPaymentDao: TripPaymentDao = mock()
    private val balanceDao: BalanceDao = mock()
    private val exchangeRateDao: ExchangeRateDao = mock()

    private lateinit var repository: TripItemRepository

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var roomStatic: MockedStatic<*>

    private val expense = TripExpense(id = 1L, paidById = 1L, amount = 30.0, currency = "EUR", tripId = 1L)
    private val payment =
        TripPayment(id = 1L, tripId = 1L, fromUserId = 1L, toUserId = 2L, amount = 10.0, currency = "EUR")
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
            db, tripCurrencyDao, tripParticipantDao, tripExpensesDao,
            tripPaymentDao, balanceDao, exchangeRateDao
        )
    }

    @After
    fun tearDown() {
        roomStatic.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `saveExpense with missing rate returns MissingExchangeRate and writes nothing`() = runTest {
        whenever(exchangeRateDao.getExchangeRateForCurrency("EUR")).thenReturn(null)

        val result = repository.saveExpenseWithParticipants(expense, participants)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripExpensesDao, never()).saveExpense(any())
        verify(tripExpensesDao, never()).saveCrossRefs(any())
        verifyNoInteractions(balanceDao)
    }

    @Test
    fun `savePayment with missing rate returns MissingExchangeRate and writes nothing`() = runTest {
        whenever(exchangeRateDao.getExchangeRateForCurrency("EUR")).thenReturn(null)

        val result = repository.savePayment(payment)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripPaymentDao, never()).savePayment(any())
        verifyNoInteractions(balanceDao)
    }

    @Test
    fun `deleteExpense with missing rate returns MissingExchangeRate and leaves DAOs untouched`() = runTest {
        whenever(tripExpensesDao.getExpenseWithParticipantsById(1L))
            .thenReturn(ExpenseWithParticipants(expense, participants.toList()))
        whenever(exchangeRateDao.getExchangeRateForCurrency("EUR")).thenReturn(null)

        val result = repository.deleteExpenseById(1L)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripExpensesDao, never()).deleteExpenseWithCrossRefs(any())
        verifyNoInteractions(balanceDao)
    }

    @Test
    fun `deletePayment with missing rate returns MissingExchangeRate and leaves DAOs untouched`() = runTest {
        whenever(tripPaymentDao.getPaymentById(1L)).thenReturn(payment)
        whenever(exchangeRateDao.getExchangeRateForCurrency("EUR")).thenReturn(null)

        val result = repository.deletePaymentById(1L)

        assertThat(result).isEqualTo(TripItemResult.MissingExchangeRate)
        verify(tripPaymentDao, never()).deletePaymentById(any())
        verifyNoInteractions(balanceDao)
    }

    @Test
    fun `saveExpense in USD succeeds with an empty rate table`() = runTest {
        val usdExpense = expense.copy(currency = "USD")
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(1L)
        whenever(balanceDao.updateBalance(any(), any(), any())).thenReturn(1)

        val result = repository.saveExpenseWithParticipants(usdExpense, participants)

        assertThat(result).isEqualTo(TripItemResult.Success)
        verify(tripExpensesDao).saveExpense(usdExpense)
        verify(exchangeRateDao, never()).getExchangeRateForCurrency(any())
    }

    @Test
    fun `saveExpense in non-USD succeeds when a rate is stored`() = runTest {
        whenever(exchangeRateDao.getExchangeRateForCurrency("EUR"))
            .thenReturn(ExchangeRate(currencyCode = "EUR", baseCurrency = "USD", rate = 0.9))
        whenever(tripExpensesDao.saveExpense(any())).thenReturn(1L)
        whenever(balanceDao.updateBalance(any(), any(), any())).thenReturn(1)

        val result = repository.saveExpenseWithParticipants(expense, participants)

        assertThat(result).isEqualTo(TripItemResult.Success)
        verify(tripExpensesDao).saveExpense(expense)
    }
}
