package com.anabars.tripsplit.data.room.converters

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

    @TypeConverter
    fun toDate(timestamp: Long): Date = Date(timestamp)
}