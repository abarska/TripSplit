package com.anabars.tripsplit.data.room

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {
    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)
}