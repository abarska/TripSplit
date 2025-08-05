package com.anabars.tripsplit.ui.screens

sealed class AppScreens(val route: String) {
    data object TripsScreen : AppScreens(ROUTE_TRIPS)
    data object AddTripScreen : AppScreens(ROUTE_ADD_TRIP)
    data object AddExpenseScreen : AppScreens(ROUTE_ADD_EXPENSE)
    data object AddPaymentScreen : AppScreens(ROUTE_ADD_PAYMENT)
    data object SettingsScreen : AppScreens(ROUTE_SETTINGS)
    data object ArchiveScreen : AppScreens(ROUTE_ARCHIVE)
    data object TripDetailsScreen : AppScreens(ROUTE_TRIP_DETAILS)

    companion object {
        const val ROUTE_TRIPS = "trips"
        const val ROUTE_ADD_TRIP = "add_trip"
        const val ROUTE_ADD_EXPENSE = "add_expense"
        const val ROUTE_ADD_PAYMENT = "add_payment"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_ARCHIVE = "archive"
        const val ROUTE_TRIP_DETAILS = "trip_details"

        private val allAppScreens = listOf(
            TripsScreen,
            AddTripScreen,
            AddExpenseScreen,
            AddPaymentScreen,
            SettingsScreen,
            ArchiveScreen,
            TripDetailsScreen
        )

        fun fromRoute(route: String?): AppScreens? {
            if (route == null) return null
            return allAppScreens.firstOrNull { it.route == route.substringBefore('/') }
        }
    }
}