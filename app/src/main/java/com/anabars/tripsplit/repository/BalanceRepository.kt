package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.model.BalancesAndCurrencies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao, private val currencyDao: TripCurrencyDao
) {
    fun getBalancesAndCurrencies(tripId: Long): Flow<BalancesAndCurrencies> {
        TODO("drift-free-balances 4.1: derive balances from expenses, shares and payments")
    }
}
