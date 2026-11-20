package com.anabars.tripsplit.utils

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Currency

object MoneyMath {

    private const val DEFAULT_FRACTION_DIGITS = 2

    fun fractionDigits(code: String): Int {
        val digits = try {
            Currency.getInstance(code).defaultFractionDigits
        } catch (_: IllegalArgumentException) {
            -1
        }
        return if (digits < 0) DEFAULT_FRACTION_DIGITS else digits
    }

    /** Returns the amount in minor units, or null when it is invalid, non-positive or has too many decimals. */
    fun parse(input: String, code: String): Long? {
        val value = input.trim().toBigDecimalOrNull() ?: return null
        if (value.signum() <= 0) return null
        if (value.stripTrailingZeros().scale() > fractionDigits(code)) return null
        return try {
            value.movePointRight(fractionDigits(code)).longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun format(minor: Long, code: String): String =
        BigDecimal.valueOf(minor, fractionDigits(code)).toPlainString()

    /** Rates are units per 1 USD, so the rate from one currency to another is toUsdRate / fromUsdRate. */
    fun crossRate(fromUsdRate: BigDecimal, toUsdRate: BigDecimal): BigDecimal =
        toUsdRate.divide(fromUsdRate, MathContext.DECIMAL128)

    fun convert(amountMinor: Long, from: String, to: String, rate: BigDecimal): Long {
        if (from == to) return amountMinor
        val shift = fractionDigits(to) - fractionDigits(from)
        return BigDecimal(amountMinor)
            .multiply(rate)
            .movePointRight(shift)
            .setScale(0, RoundingMode.HALF_EVEN)
            .longValueExact()
    }
}
