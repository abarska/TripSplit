package com.anabars.tripsplit.data.room.model

import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.TripCurrency
import java.math.BigDecimal

data class BalancesAndCurrencies(
    val balances: List<BalanceWithNameAndStatus>,
    val currencies: List<TripCurrency>,
    val tripCurrency: String = TripSplitConstants.BASE_CURRENCY
)

/** [amount] is in the trip currency; [approxAmount] is the current-rate value in the home currency, when known. */
data class BalanceWithNameAndStatus(
    val participantName: String,
    val participantStatus: ParticipantStatus,
    val amount: BigDecimal,
    val amountCurrency: String = TripSplitConstants.BASE_CURRENCY,
    val approxAmount: BigDecimal? = null,
    val approxCurrency: String? = null
)
