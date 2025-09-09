package com.anabars.tripsplit.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import com.anabars.tripsplit.navigation.AppScreens.TRIPS

fun NavController.isAtStartDestination(): Boolean = this.currentDestination?.hierarchy?.any { it.route == TRIPS.route } == true