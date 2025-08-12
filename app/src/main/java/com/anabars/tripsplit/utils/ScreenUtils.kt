package com.anabars.tripsplit.utils

import android.content.res.Configuration

fun isTablet(configuration: Configuration) = configuration.smallestScreenWidthDp >= 600

fun isLandscapeSmallOrAnyBig(configuration: Configuration) =
    configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet(configuration)