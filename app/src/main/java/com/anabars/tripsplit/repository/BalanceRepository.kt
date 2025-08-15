package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.entity.ParticipantBalance
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao,
    private val exchangeRateDao: ExchangeRateDao
) {

    fun getBalancesForTrip(tripId: Long): Flow<List<ParticipantBalance>> =
        balanceDao.getBalancesForTrip(tripId)
}
