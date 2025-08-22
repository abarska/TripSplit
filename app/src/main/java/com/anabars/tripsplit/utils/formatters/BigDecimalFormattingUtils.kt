package com.anabars.tripsplit.utils.formatters

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.formatAsCurrency(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
