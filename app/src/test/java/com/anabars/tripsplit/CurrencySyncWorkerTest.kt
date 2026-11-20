package com.anabars.tripsplit

import com.anabars.tripsplit.data.network.CurrencyResponse
import com.anabars.tripsplit.workers.toExchangeRates
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class CurrencySyncWorkerTest {

    @Test
    fun `valid rate is stored as BigDecimal`() {
        val result = CurrencyResponse("2026-01-01", "USD", mapOf("EUR" to "0.9137")).toExchangeRates()

        assertThat(result).hasSize(1)
        assertThat(result[0].currencyCode).isEqualTo("EUR")
        assertThat(result[0].rate).isEqualTo(BigDecimal("0.9137"))
    }

    @Test
    fun `unparseable and non-positive rates are skipped`() {
        val response = CurrencyResponse(
            "2026-01-01", "USD",
            mapOf("EUR" to "0.9137", "ABC" to "abc", "ZER" to "0", "NEG" to "-1.5")
        )

        assertThat(response.toExchangeRates().map { it.currencyCode }).containsExactly("EUR")
    }
}
