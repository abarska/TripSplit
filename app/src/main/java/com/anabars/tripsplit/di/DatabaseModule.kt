package com.anabars.tripsplit.di

import android.content.Context
import androidx.room.Room
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.dao.TripCurrencyDao
import com.anabars.tripsplit.data.room.dao.TripParticipantDao
import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripExpensesDao
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.room.dao.BalanceDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TripSplitDatabase =
        Room.databaseBuilder(
            context,
            TripSplitDatabase::class.java,
            TripSplitConstants.TRIP_SPLIT_DATABASE
        ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideTripDao(db: TripSplitDatabase): TripDao = db.tripDao()

    @Provides
    @Singleton
    fun provideTripParticipantDao(db: TripSplitDatabase): TripParticipantDao = db.tripParticipantDao()

    @Provides
    @Singleton
    fun provideTripCurrencyDao(db: TripSplitDatabase): TripCurrencyDao = db.tripCurrencyDao()

    @Provides
    @Singleton
    fun provideTripExpensesDao(db: TripSplitDatabase): TripExpensesDao = db.tripExpensesDao()

    @Provides
    @Singleton
    fun provideParticipantBalanceDao(db: TripSplitDatabase): BalanceDao = db.balanceDao()

    @Provides
    @Singleton
    fun provideTripPaymentDao(db: TripSplitDatabase): TripPaymentDao = db.tripPaymentDao()

    @Provides
    @Singleton
    fun provideExchangeRateDao(db: TripSplitDatabase): ExchangeRateDao = db.exchangeRateDao()
}
