package com.anabars.tripsplit.ui.screens

import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import com.anabars.tripsplit.R

sealed class AppScreens(
    val route: String,
    @StringRes val title: Int,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object TripsScreen : AppScreens(ROUTE_TRIPS, R.string.title_trips)
    data object AddTripScreen : AppScreens(ROUTE_ADD_TRIP, R.string.title_new_trip)
    data object JoinExistingTripScreen :
        AppScreens(ROUTE_EXISTING_TRIP, R.string.title_join_existing_trip)

    data object SettingsScreen : AppScreens(ROUTE_SETTINGS, R.string.title_settings)
    data object TripDetailsScreen : AppScreens(ROUTE_TRIP_DETAILS, R.string.title_trip_details)

    companion object {
        const val ROUTE_TRIPS = "trips"
        const val ROUTE_ADD_TRIP = "add_trip"
        const val ROUTE_EXISTING_TRIP = "existing_trip"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_TRIP_DETAILS = "trip_details"

        private val allAppScreens = listOf(
            TripsScreen,
            AddTripScreen,
            JoinExistingTripScreen,
            SettingsScreen,
            TripDetailsScreen
        )

        fun fromRoute(route: String?): AppScreens? {
            if (route == null) return null
            return allAppScreens.firstOrNull { it.route == route.substringBefore('/') }
        }
    }
}