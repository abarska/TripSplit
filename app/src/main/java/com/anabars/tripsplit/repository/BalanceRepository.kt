package com.anabars.tripsplit.repository

import com.anabars.tripsplit.utils.MoneyMath
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.data.room.model.BalancesAndCurrencies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao, private val currencyDao: TripCurrencyDao
) {
    fun getBalancesAndCurrencies(tripId: Long): Flow<BalancesAndCurrencies> = combine(
        balanceDao.getBalances(tripId),
        balanceDao.getTripCurrency(tripId).filterNotNull(),
        currencyDao.getCurrenciesByTripIdAsFlow(tripId)
    ) { rows, tripCurrency, currencies ->
        val digits = MoneyMath.fractionDigits(tripCurrency)
        BalancesAndCurrencies(
            balances = rows.map {
                BalanceWithNameAndStatus(
                    participantName = it.name,
                    participantStatus = it.status,
                    amount = BigDecimal.valueOf(it.balanceMinor, digits),
                    amountCurrency = tripCurrency
                )
            },
            currencies = currencies,
            tripCurrency = tripCurrency
        )
    }
}
