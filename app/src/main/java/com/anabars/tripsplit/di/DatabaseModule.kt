package com.anabars.tripsplit.di

import android.content.Context
import androidx.room.Room
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.ExchangeRateDao
import com.anabars.tripsplit.data.room.TripCurrencyDao
import com.anabars.tripsplit.data.room.TripParticipantDao
import com.anabars.tripsplit.data.room.TripDao
import com.anabars.tripsplit.data.room.TripSplitDatabase
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
    fun provideExchangeRateDao(db: TripSplitDatabase): ExchangeRateDao = db.exchangeRateDao()
}
