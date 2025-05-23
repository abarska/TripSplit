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
    data object JoinExistingTripScreen : AppScreens(ROUTE_EXISTING_TRIP, R.string.title_join_existing_trip)
    data object SettingsScreen : AppScreens(ROUTE_SETTINGS, R.string.title_settings)

    companion object {
        const val ROUTE_TRIPS = "trips"
        const val ROUTE_ADD_TRIP = "add_trip"
        const val ROUTE_EXISTING_TRIP = "existing_trip"
        const val ROUTE_SETTINGS = "settings"

        fun fromRoute(route: String?): AppScreens? = when (route) {
            TripsScreen.route -> TripsScreen
            AddTripScreen.route -> AddTripScreen
            JoinExistingTripScreen.route -> JoinExistingTripScreen
            SettingsScreen.route -> SettingsScreen
            else -> null
        }
    }
}