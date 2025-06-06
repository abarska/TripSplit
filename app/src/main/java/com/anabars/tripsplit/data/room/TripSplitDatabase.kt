package com.anabars.tripsplit.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anabars.tripsplit.model.ExchangeRate
import com.anabars.tripsplit.model.TripParticipant
import com.anabars.tripsplit.model.Trip
import com.anabars.tripsplit.model.TripCurrency

@Database(
    entities = [Trip::class, TripParticipant::class, TripCurrency::class, ExchangeRate::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class TripSplitDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun tripParticipantDao(): TripParticipantDao
    abstract fun tripCurrencyDao(): TripCurrencyDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}