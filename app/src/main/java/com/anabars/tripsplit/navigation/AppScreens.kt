package com.anabars.tripsplit.navigation

enum class AppScreens(val route: String) {
    TRIPS("trips"),
    ADD_TRIP("add_trip"),
    TRIP_OVERVIEW("trip_overview"),
    ADD_EXPENSE("add_expense"),
    ADD_PAYMENT("add_payment"),
    SETTINGS("settings"),
    ARCHIVE("archive"),
    TRIP_DETAILS("trip_details");

    companion object {
        fun getAll() = entries.map { it.route }.toTypedArray()
    }
}