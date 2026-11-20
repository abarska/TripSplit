package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.preferences.CurrencyPreference
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.repository.TripItemResult
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.screens.addtrip.AddTripIntent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AddTripViewModelTest {

    private val repository: TripRepository = mock()
    private val currencyPreference: CurrencyPreference = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(currencyPreference.getCurrencyFlow(any())).thenReturn(flowOf("UAH"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun newTripViewModel(): AddTripViewModel {
        val vm = AddTripViewModel(SavedStateHandle(), repository, currencyPreference)
        vm.uiState.first { it.defaultCurrency.isNotBlank() }
        return vm
    }

    private suspend fun editTripViewModel(defaultCurrency: String): AddTripViewModel {
        val details = TripDetails(
            trip = Trip(id = 1L, title = "Trip", defaultCurrency = defaultCurrency),
            participants = emptyList(),
            currencies = listOf(
                TripCurrency(code = "UAH", tripId = 1L),
                TripCurrency(code = "EUR", tripId = 1L)
            )
        )
        whenever(repository.getTripDetailsData(1L)).thenReturn(details)
        val vm = AddTripViewModel(SavedStateHandle(mapOf("tripId" to 1L)), repository, currencyPreference)
        vm.uiState.first { it.defaultCurrency.isNotBlank() && it.availableCurrencies.isNotEmpty() }
        return vm
    }

    @Test
    fun `new trip preselects the home currency as default`() = runTest {
        val vm = newTripViewModel()

        assertThat(vm.uiState.value.tripCurrencyCodes).containsExactly("UAH")
        assertThat(vm.uiState.value.defaultCurrency).isEqualTo("UAH")
    }

    @Test
    fun `another trip currency can be made the default`() = runTest {
        val vm = newTripViewModel()
        vm.onIntent(AddTripIntent.CurrencyAdded("EUR"))

        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("EUR"))

        assertThat(vm.uiState.value.defaultCurrency).isEqualTo("EUR")
    }

    @Test
    fun `a currency outside the trip cannot be made the default`() = runTest {
        val vm = newTripViewModel()

        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("EUR"))

        assertThat(vm.uiState.value.defaultCurrency).isEqualTo("UAH")
    }

    @Test
    fun `default currency cannot be removed until another is made default`() = runTest {
        val vm = newTripViewModel()
        vm.onIntent(AddTripIntent.CurrencyAdded("EUR"))
        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("EUR"))

        vm.onIntent(AddTripIntent.CurrencyDeleted("EUR"))
        assertThat(vm.uiState.value.tripCurrencyCodes).containsExactly("UAH", "EUR")

        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("UAH"))
        vm.onIntent(AddTripIntent.CurrencyDeleted("EUR"))
        assertThat(vm.uiState.value.tripCurrencyCodes).containsExactly("UAH")
    }

    @Test
    fun `saving passes the chosen default currency to the repository`() = runTest {
        whenever(repository.saveTrip(anyOrNull(), any(), any(), any())).thenReturn(TripItemResult.Success)
        val vm = newTripViewModel()
        vm.onIntent(AddTripIntent.CurrencyAdded("EUR"))
        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("EUR"))
        vm.onIntent(AddTripIntent.TripNameChanged("Rome"))

        vm.onIntent(AddTripIntent.SaveTripClicked)

        val trip = argumentCaptor<Trip>()
        org.mockito.kotlin.verify(repository).saveTrip(eq(null), trip.capture(), any(), any())
        assertThat(trip.firstValue.defaultCurrency).isEqualTo("EUR")
    }

    @Test
    fun `refused currency switch shows snackbar and does not navigate back`() = runTest {
        whenever(repository.saveTrip(any(), any(), any(), any()))
            .thenReturn(TripItemResult.MissingExchangeRate)
        val vm = editTripViewModel(defaultCurrency = "UAH")
        val snackbars = mutableListOf<Int>()
        val navigations = mutableListOf<Boolean>()
        val job1 = launch(UnconfinedTestDispatcher(testScheduler)) { vm.showSnackbar.collect { snackbars.add(it) } }
        val job2 = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigateBackWithWarning.collect { navigations.add(it) }
        }
        vm.onIntent(AddTripIntent.DefaultCurrencyChanged("EUR"))

        vm.onIntent(AddTripIntent.SaveTripClicked)

        assertThat(snackbars).containsExactly(R.string.error_save_exchange_rates_unavailable)
        assertThat(navigations).isEmpty()
        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `successful save navigates back without warning`() = runTest {
        whenever(repository.saveTrip(any(), any(), any(), any())).thenReturn(TripItemResult.Success)
        val vm = editTripViewModel(defaultCurrency = "UAH")
        val navigations = mutableListOf<Boolean>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigateBackWithWarning.collect { navigations.add(it) }
        }

        vm.onIntent(AddTripIntent.SaveTripClicked)

        assertThat(navigations).containsExactly(false)
        job.cancel()
    }
}
