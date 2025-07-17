package com.anabars.tripsplit.ui.screens

sealed class AppScreens(val route: String) {
    data object TripsScreen : AppScreens(ROUTE_TRIPS)
    data object AddTripScreen : AppScreens(ROUTE_ADD_TRIP)
    data object AddExpenseScreen : AppScreens(ROUTE_ADD_EXPENSE)
    data object SettingsScreen : AppScreens(ROUTE_SETTINGS)
    data object TripDetailsScreen : AppScreens(ROUTE_TRIP_DETAILS)

    companion object {
        const val ROUTE_TRIPS = "trips"
        const val ROUTE_ADD_TRIP = "add_trip"
        const val ROUTE_ADD_EXPENSE = "add_expense"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_TRIP_DETAILS = "trip_details"

        private val allAppScreens = listOf(
            TripsScreen,
            AddTripScreen,
            AddExpenseScreen,
            SettingsScreen,
            TripDetailsScreen
        )

        fun fromRoute(route: String?): AppScreens? {
            if (route == null) return null
            return allAppScreens.firstOrNull { it.route == route.substringBefore('/') }
        }
    }
}