package com.anabars.tripsplit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TripSplitApplication : Application() {

    companion object {
        lateinit var instance: TripSplitApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}