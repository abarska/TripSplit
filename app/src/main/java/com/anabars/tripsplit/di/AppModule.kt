package com.anabars.tripsplit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.anabars.tripsplit.BuildConfig
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.ParticipantDao
import com.anabars.tripsplit.data.room.TripSplitDatabase
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.network.CurrencyApiService
import com.anabars.tripsplit.data.preferences.ExchangeRatePreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): TripSplitDatabase =
        Room.databaseBuilder(
            context,
            TripSplitDatabase::class.java,
            name = TripSplitConstants.TRIP_SPLIT_DATABASE
        )
            .fallbackToDestructiveMigration(false).build()

    @Singleton
    @Provides
    fun provideTripDao(db: TripSplitDatabase) = db.tripDao()

    @Singleton
    @Provides
    fun provideParticipantDao(db: TripSplitDatabase): ParticipantDao = db.participantDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(TripSplitConstants.DATASTORE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideCurrencyPreference(dataStore: DataStore<Preferences>): CurrencyPreference =
        CurrencyPreference(dataStore)

    @Provides
    @Singleton
    fun provideExchangeRatesPreference(dataStore: DataStore<Preferences>): ExchangeRatePreference =
        ExchangeRatePreference(dataStore)

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApiService =
        retrofit.create(CurrencyApiService::class.java)
}