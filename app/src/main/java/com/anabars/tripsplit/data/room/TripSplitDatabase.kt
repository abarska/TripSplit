package com.anabars.tripsplit.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anabars.tripsplit.data.room.converters.DateConverter
import com.anabars.tripsplit.data.room.converters.ExpenseCategoryConverter
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripSettlementDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.ExpenseParticipantCrossRef
import com.anabars.tripsplit.data.room.entity.TripSettlement
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant

@Database(
    entities = [
        Trip::class,
        TripParticipant::class,
        TripCurrency::class,
        TripExpense::class,
        TripSettlement::class,
        ExchangeRate::class,
        ExpenseParticipantCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ExpenseCategoryConverter::class)
abstract class TripSplitDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun tripParticipantDao(): TripParticipantDao
    abstract fun tripCurrencyDao(): TripCurrencyDao
    abstract fun tripExpensesDao(): TripExpensesDao
    abstract fun tripSettlementDao(): TripSettlementDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}