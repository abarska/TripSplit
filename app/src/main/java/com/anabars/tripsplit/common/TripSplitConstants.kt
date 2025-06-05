package com.anabars.tripsplit.common

object TripSplitConstants {

    // DataStore
    const val DATASTORE_NAME = "user_settings"
    const val PREF_KEY_LOCAL_CURRENCY = "local_currency"

    // Room
    const val TRIP_SPLIT_DATABASE = "trip_split_db"
    const val TRIP_TABLE = "trip_table"
    const val PARTICIPANT_TABLE = "participant_table"
    const val EXCHANGE_RATE_TABLE = "exchange_rate_table"

    // workers
    const val CURRENCY_SYNC_IMMEDIATE_WORK_NAME = "immediate_currency_sync"
    const val CURRENCY_SYNC_PERIODIC_WORK_NAME = "periodic_currency_sync"
}