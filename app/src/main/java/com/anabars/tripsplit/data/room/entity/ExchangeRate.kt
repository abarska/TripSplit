package com.anabars.tripsplit.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants
import java.math.BigDecimal

@Entity(tableName = TripSplitConstants.EXCHANGE_RATE_TABLE)
data class ExchangeRate(
    @PrimaryKey val currencyCode: String,
    val baseCurrency: String,
    val rate: BigDecimal,
    val date: String = System.currentTimeMillis().toString()
)