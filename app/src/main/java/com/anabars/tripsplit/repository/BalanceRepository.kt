package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.model.BalancesAndCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao, private val currencyDao: TripCurrencyDao
) {
    fun getBalancesAndCurrencies(tripId: Long): Flow<BalancesAndCurrencies> {
        return combine(
            balanceDao.getBalancesWithNameAndStatus(tripId),
            currencyDao.getActiveCurrenciesByTripId(tripId)
        ) { balances, currencies ->
            BalancesAndCurrencies(balances, currencies)
        }
    }
}
