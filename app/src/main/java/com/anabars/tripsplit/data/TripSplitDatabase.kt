package com.anabars.tripsplit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anabars.tripsplit.model.Trip

@Database(entities = [Trip::class], version = 1, exportSchema = false)
abstract class TripSplitDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}