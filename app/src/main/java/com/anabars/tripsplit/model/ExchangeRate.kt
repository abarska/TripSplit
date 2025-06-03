package com.anabars.tripsplit.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anabars.tripsplit.common.TripSplitConstants

@Entity(tableName = TripSplitConstants.EXCHANGE_RATE_TABLE)
data class ExchangeRate(
    @PrimaryKey val currencyCode: String,
    val baseCurrency: String,
    val rate: Double = 0.0,
    val date: String
)