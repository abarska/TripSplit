package com.anabars.tripsplit.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.dao.ExchangeRateDao
import com.anabars.tripsplit.data.room.entity.ExchangeRate
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import com.anabars.tripsplit.data.room.model.BalancesAndCurrencies
import com.anabars.tripsplit.repository.BalanceRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class BalanceViewModelTest {

    private val balanceRepository: BalanceRepository = mock()
    private val exchangeRateDao: ExchangeRateDao = mock()
    private val dataStore: DataStore<Preferences> = mock()
    private val rates = MutableStateFlow<List<ExchangeRate>>(emptyList())

    private val balance = BalanceWithNameAndStatus("A", ParticipantStatus.ACTIVE, BigDecimal("10.00"), "EUR")

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(exchangeRateDao.getRateFlow(any())).thenAnswer { invocation ->
            val code = invocation.getArgument<String>(0)
            rates.map { list -> list.firstOrNull { it.currencyCode == code }?.rate }
        }
        // run the DAO's base-currency rule for real so it reaches the stubbed rate flow
        doCallRealMethod().whenever(exchangeRateDao).getRateFlowForCurrency(any())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun prefsWith(homeCurrency: String): Preferences =
        preferencesOf(stringPreferencesKey(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY) to homeCurrency)

    private fun createViewModel(
        homeCurrency: String,
        tripCurrency: String = "EUR"
    ): BalanceViewModel = createViewModel(flowOf(prefsWith(homeCurrency)), tripCurrency)

    private fun createViewModel(prefs: Flow<Preferences>, tripCurrency: String = "EUR"): BalanceViewModel {
        whenever(balanceRepository.getBalancesAndCurrencies(1L)).thenReturn(
            flowOf(
                BalancesAndCurrencies(
                    listOf(balance.copy(amountCurrency = tripCurrency)), emptyList(), tripCurrency
                )
            )
        )
        whenever(dataStore.data).thenReturn(prefs)
        return BalanceViewModel(
            balanceRepository, exchangeRateDao, CurrencyPreference(dataStore),
            SavedStateHandle(mapOf("tripId" to 1L))
        )
    }

    @Test
    fun `open balances before first sync shows trip currency without approximate values`() = runTest {
        val vm = createViewModel("UAH")

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("10.00"))
        assertThat(shown.amountCurrency).isEqualTo("EUR")
        assertThat(shown.approxAmount).isNull()
        assertThat(shown.approxCurrency).isNull()
    }

    @Test
    fun `rates arriving later make approximate values appear`() = runTest {
        val vm = createViewModel("UAH")
        assertThat(vm.uiState.value.balances.single().approxAmount).isNull()

        rates.value = listOf(
            ExchangeRate("EUR", "USD", BigDecimal("0.5")),
            ExchangeRate("UAH", "USD", BigDecimal("40"))
        )

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("10.00"))
        assertThat(shown.approxAmount).isEqualTo(BigDecimal("800.00"))
        assertThat(shown.approxCurrency).isEqualTo("UAH")
    }

    @Test
    fun `only the home rate missing hides approximate values`() = runTest {
        rates.value = listOf(ExchangeRate("EUR", "USD", BigDecimal("0.5")))

        val vm = createViewModel("UAH")

        assertThat(vm.uiState.value.balances.single().approxAmount).isNull()
    }

    @Test
    fun `USD home currency with a missing trip rate shows no approximate values`() = runTest {
        val vm = createViewModel("USD")

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("10.00"))
        assertThat(shown.approxAmount).isNull()
    }

    @Test
    fun `home currency equal to trip currency shows trip currency only`() = runTest {
        val vm = createViewModel("UAH", tripCurrency = "UAH")

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amountCurrency).isEqualTo("UAH")
        assertThat(shown.approxAmount).isNull()
    }

    @Test
    fun `rate sync changes only the approximate value`() = runTest {
        rates.value = listOf(
            ExchangeRate("EUR", "USD", BigDecimal("0.5")),
            ExchangeRate("UAH", "USD", BigDecimal("40"))
        )
        val vm = createViewModel("UAH")
        assertThat(vm.uiState.value.balances.single().approxAmount).isEqualTo(BigDecimal("800.00"))

        rates.value = listOf(
            ExchangeRate("EUR", "USD", BigDecimal("0.5")),
            ExchangeRate("UAH", "USD", BigDecimal("50"))
        )

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("10.00"))
        assertThat(shown.approxAmount).isEqualTo(BigDecimal("1000.00"))
    }

    @Test
    fun `changing home currency preference updates approximate values`() = runTest {
        rates.value = listOf(
            ExchangeRate("EUR", "USD", BigDecimal("0.5")),
            ExchangeRate("UAH", "USD", BigDecimal("40"))
        )
        val prefs = MutableStateFlow(prefsWith("EUR"))
        val vm = createViewModel(prefs)
        assertThat(vm.uiState.value.balances.single().approxAmount).isNull()

        prefs.value = prefsWith("UAH")

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.approxAmount).isEqualTo(BigDecimal("800.00"))
        assertThat(shown.approxCurrency).isEqualTo("UAH")
    }
}
