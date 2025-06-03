package com.anabars.tripsplit.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anabars.tripsplit.model.ExchangeRate
import com.anabars.tripsplit.model.Participant
import com.anabars.tripsplit.model.Trip

@Database(entities = [Trip::class, Participant::class, ExchangeRate::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class TripSplitDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun participantDao(): ParticipantDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}