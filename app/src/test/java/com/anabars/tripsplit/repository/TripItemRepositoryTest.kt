package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.mock

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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TripItemRepository(
            db, tripCurrencyDao, tripParticipantDao, tripExpensesDao,
            tripPaymentDao, balanceDao, exchangeRateDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
