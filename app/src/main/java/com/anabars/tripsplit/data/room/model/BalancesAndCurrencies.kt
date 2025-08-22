package com.anabars.tripsplit.data.room.model

import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.TripCurrency
import java.math.BigDecimal

data class BalancesAndCurrencies(
    val balances: List<BalanceWithNameAndStatus>,
    val currencies: List<TripCurrency>
)

data class BalanceWithNameAndStatus(
    val participantName: String,
    val participantStatus: ParticipantStatus,
    val amount: BigDecimal,
    val amountCurrency: String = TripSplitConstants.BASE_CURRENCY
)