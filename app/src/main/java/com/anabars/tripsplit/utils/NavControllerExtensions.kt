package com.anabars.tripsplit.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavController.isAtStartDestination(startDestination: String) =
    this.currentDestination?.hierarchy?.any { it.route == startDestination } == true