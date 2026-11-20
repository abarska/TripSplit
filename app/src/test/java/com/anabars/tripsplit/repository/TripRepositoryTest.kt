package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
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
            db, tripDao, tripExpensesDao, participantDao, exchangeRateDao, currencyDao
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
}
