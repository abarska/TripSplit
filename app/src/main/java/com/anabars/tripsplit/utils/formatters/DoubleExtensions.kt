package com.anabars.tripsplit.utils.formatters

import java.text.DecimalFormat

fun Double.formatAsCurrency(pattern: String): String = DecimalFormat(pattern).format(this)