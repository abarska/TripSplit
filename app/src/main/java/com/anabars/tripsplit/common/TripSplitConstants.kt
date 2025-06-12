package com.anabars.tripsplit.common

object TripSplitConstants {

    // DataStore
    const val DATASTORE_NAME = "user_settings"
    const val PREF_KEY_LOCAL_CURRENCY = "local_currency"

    // Room
    const val TRIP_SPLIT_DATABASE = "trip_split_db"
    const val TRIP_TABLE = "trip_table"
    const val TRIP_PARTICIPANTS_TABLE = "trip_participants_table"
    const val TRIP_CURRENCIES_TABLE = "trip_currencies_table"
    const val TRIP_EXPENSES_TABLE = "trip_expenses_table"
    const val EXCHANGE_RATE_TABLE = "exchange_rate_table"
    const val CROSS_TABLE_EXPENSE_PARTICIPANT= "cross_table_expense_participant"

    // workers
    const val CURRENCY_SYNC_IMMEDIATE_WORK_NAME = "immediate_currency_sync"
    const val CURRENCY_SYNC_PERIODIC_WORK_NAME = "periodic_currency_sync"
}