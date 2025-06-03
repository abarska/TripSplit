package com.anabars.tripsplit

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.anabars.tripsplit.workers.CurrencySyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TripSplitApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var currencySyncScheduler: CurrencySyncScheduler

    companion object {
        lateinit var instance: TripSplitApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Schedule after injection is done
        Handler(Looper.getMainLooper()).post {
            currencySyncScheduler.scheduleCurrencySync()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}