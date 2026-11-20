package com.anabars.tripsplit.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.common.TripSplitConstants
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

    private val balance = BalanceWithNameAndStatus("A", ParticipantStatus.ACTIVE, BigDecimal("10"))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(balanceRepository.getBalancesAndCurrencies(1L))
            .thenReturn(flowOf(BalancesAndCurrencies(listOf(balance), emptyList())))
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

    private fun prefsWith(homeCurrency: String?): Preferences =
        if (homeCurrency == null) preferencesOf()
        else preferencesOf(stringPreferencesKey(TripSplitConstants.PREF_KEY_LOCAL_CURRENCY) to homeCurrency)

    private fun createViewModel(homeCurrency: String?): BalanceViewModel =
        createViewModel(flowOf(prefsWith(homeCurrency)))

    private fun createViewModel(prefs: Flow<Preferences>): BalanceViewModel {
        whenever(dataStore.data).thenReturn(prefs)
        return BalanceViewModel(balanceRepository, exchangeRateDao, dataStore, SavedStateHandle(mapOf("tripId" to 1L)))
    }

    @Test
    fun `missing home rate shows rates unavailable without crashing`() = runTest {
        val vm = createViewModel("UAH")

        assertThat(vm.uiState.value.ratesAvailable).isFalse()
        assertThat(vm.uiState.value.balances).isEmpty()
    }

    @Test
    fun `rate arriving later makes converted balances appear`() = runTest {
        val vm = createViewModel("UAH")
        assertThat(vm.uiState.value.ratesAvailable).isFalse()

        rates.value = listOf(ExchangeRate("UAH", "USD", BigDecimal("40")))

        assertThat(vm.uiState.value.ratesAvailable).isTrue()
        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("400"))
        assertThat(shown.amountCurrency).isEqualTo("UAH")
    }

    @Test
    fun `USD home currency shows balances with an empty rate table`() = runTest {
        val vm = createViewModel("USD")

        assertThat(vm.uiState.value.ratesAvailable).isTrue()
        assertThat(vm.uiState.value.balances.single().amount).isEqualTo(BigDecimal("10"))
    }

    @Test
    fun `absent home currency preference defaults to USD`() = runTest {
        val vm = createViewModel(null)

        assertThat(vm.uiState.value.ratesAvailable).isTrue()
    }

    @Test
    fun `changing home currency preference updates converted balances`() = runTest {
        val prefs = MutableStateFlow(prefsWith("USD"))
        rates.value = listOf(ExchangeRate("UAH", "USD", BigDecimal("40")))
        val vm = createViewModel(prefs)
        assertThat(vm.uiState.value.balances.single().amountCurrency).isEqualTo("USD")

        prefs.value = prefsWith("UAH")

        val shown = vm.uiState.value.balances.single()
        assertThat(shown.amount).isEqualTo(BigDecimal("400"))
        assertThat(shown.amountCurrency).isEqualTo("UAH")
    }
}
