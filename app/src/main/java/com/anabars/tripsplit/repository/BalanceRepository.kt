package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.BalanceDao
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao
) {

    fun getBalancesWithNameAndStatus(tripId: Long) =
        balanceDao.getBalancesWithNameAndStatus(tripId)
}
