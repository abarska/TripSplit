package com.anabars.tripsplit.utils

import com.anabars.tripsplit.navigation.AppScreens.TRIP_DETAILS

fun String?.startsWithTripDetailsRoute(): Boolean = this?.startsWith(TRIP_DETAILS.route) == true