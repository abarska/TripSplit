package com.anabars.tripsplit.di

import android.content.Context
import androidx.room.Room
import com.anabars.tripsplit.data.TripSplitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): TripSplitDatabase =
        Room.databaseBuilder(context, TripSplitDatabase::class.java, name = "trip_split_db")
            .fallbackToDestructiveMigration(false).build()

    @Singleton
    @Provides
    fun provideTripDao(tripSplitDatabase: TripSplitDatabase) = tripSplitDatabase.tripDao()
}