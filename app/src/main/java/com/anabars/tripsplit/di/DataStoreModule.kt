package com.anabars.tripsplit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.preferences.ExchangeRatePreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(TripSplitConstants.DATASTORE_NAME)
        }

    @Provides
    @Singleton
    fun provideCurrencyPreference(dataStore: DataStore<Preferences>): CurrencyPreference =
        CurrencyPreference(dataStore)

    @Provides
    @Singleton
    fun provideExchangeRatesPreference(dataStore: DataStore<Preferences>): ExchangeRatePreference =
        ExchangeRatePreference(dataStore)
}