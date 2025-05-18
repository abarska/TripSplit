package com.anabars.tripsplit.navigation.screens

import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.anabars.tripsplit.R

sealed class AppScreens(
    val route: String,
    @StringRes val title: Int,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object HomeScreen : AppScreens(ROUTE_HOME, R.string.title_home)
    data object TripsScreen : AppScreens(
        "$ROUTE_TRIPS/{itemId}",
        R.string.title_trips,
        listOf(navArgument("itemId") { type = NavType.StringType })
    )

    data object SettingsScreen : AppScreens(ROUTE_SETTINGS, R.string.title_settings)

    companion object {
        const val ROUTE_HOME = "home"
        const val ROUTE_TRIPS = "trips"
        const val ROUTE_SETTINGS = "settings"

        fun fromRoute(route: String?): AppScreens? = when {
            route == HomeScreen.route -> HomeScreen
            route?.startsWith(TripsScreen.route.substringBefore("/")) == true -> TripsScreen
            route == SettingsScreen.route -> SettingsScreen
            else -> null
        }
    }
}