package com.anabars.tripsplit

import com.anabars.tripsplit.utils.MoneyMath
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class MoneyMathTest {

    @Test
    fun `fraction digits follow the currency`() {
        assertThat(MoneyMath.fractionDigits("EUR")).isEqualTo(2)
        assertThat(MoneyMath.fractionDigits("JPY")).isEqualTo(0)
        assertThat(MoneyMath.fractionDigits("KWD")).isEqualTo(3)
    }

    @Test
    fun `fraction digits fall back to 2 for unknown codes`() {
        assertThat(MoneyMath.fractionDigits("XXZ")).isEqualTo(2)
    }

    @Test
    fun `parse rejects too many decimals and accepts exact ones`() {
        assertThat(MoneyMath.parse("12.345", "EUR")).isNull()
        assertThat(MoneyMath.parse("12.34", "EUR")).isEqualTo(1234L)
        assertThat(MoneyMath.parse("12.340", "EUR")).isEqualTo(1234L)
        assertThat(MoneyMath.parse("500", "JPY")).isEqualTo(500L)
        assertThat(MoneyMath.parse("1.5", "JPY")).isNull()
        assertThat(MoneyMath.parse("1.234", "KWD")).isEqualTo(1234L)
    }

    @Test
    fun `parse rejects non-positive and invalid input`() {
        assertThat(MoneyMath.parse("0", "EUR")).isNull()
        assertThat(MoneyMath.parse("-5", "EUR")).isNull()
        assertThat(MoneyMath.parse("abc", "EUR")).isNull()
        assertThat(MoneyMath.parse("", "EUR")).isNull()
    }

    @Test
    fun `format uses the currency fraction digits`() {
        assertThat(MoneyMath.format(1234, "EUR")).isEqualTo("12.34")
        assertThat(MoneyMath.format(5, "EUR")).isEqualTo("0.05")
        assertThat(MoneyMath.format(500, "JPY")).isEqualTo("500")
        assertThat(MoneyMath.format(1234, "KWD")).isEqualTo("1.234")
    }

    @Test
    fun `crossRate divides the target rate by the source rate`() {
        val rate = MoneyMath.crossRate(BigDecimal("2"), BigDecimal("3"))
        assertThat(rate.compareTo(BigDecimal("1.5"))).isEqualTo(0)
    }

    @Test
    fun `100 USD at 0_9137 gives 9137 EUR minor units`() {
        assertThat(MoneyMath.convert(10000, "USD", "EUR", BigDecimal("0.9137"))).isEqualTo(9137L)
    }

    @Test
    fun `convert adjusts for different fraction digits`() {
        assertThat(MoneyMath.convert(10000, "USD", "JPY", BigDecimal("150"))).isEqualTo(15000L)
        assertThat(MoneyMath.convert(15000, "JPY", "USD", BigDecimal("0.0066667"))).isEqualTo(10000L)
    }

    @Test
    fun `same-currency convert is the identity`() {
        assertThat(MoneyMath.convert(1234, "EUR", "EUR", BigDecimal("0.5"))).isEqualTo(1234L)
    }

    @Test
    fun `halves round to even`() {
        assertThat(MoneyMath.convert(5, "EUR", "GBP", BigDecimal("0.5"))).isEqualTo(2L)
        assertThat(MoneyMath.convert(15, "EUR", "GBP", BigDecimal("0.5"))).isEqualTo(8L)
        assertThat(MoneyMath.convert(25, "EUR", "GBP", BigDecimal("0.5"))).isEqualTo(12L)
    }
}
