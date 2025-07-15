package com.anabars.tripsplit.utils

import java.text.DecimalFormat

fun formatAmount(amount: Double, pattern: String): String {
    return DecimalFormat(pattern).format(amount)
}