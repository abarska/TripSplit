package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.R
import com.anabars.tripsplit.repository.TripItemRepository
import com.anabars.tripsplit.repository.TripItemResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TripItemViewModelTest {

    private val repository: TripItemRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(repository.getExpensesWithParticipantsByTrip(1L)).thenReturn(flowOf(emptyList()))
        whenever(repository.getPaymentsByTripId(1L)).thenReturn(flowOf(emptyList()))
        whenever(repository.getParticipantsByTripId(1L)).thenReturn(flowOf(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        TripItemViewModel(repository, SavedStateHandle(mapOf("tripId" to 1L)))

    @Test
    fun `refused expense delete emits snackbar`() = runTest {
        whenever(repository.deleteExpenseById(5L)).thenReturn(TripItemResult.MissingExchangeRate)
        val vm = createViewModel()
        val messages = mutableListOf<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.snackbarEffect.collect { messages.add(it) } }

        vm.onIntent(DeleteItemIntent.DeleteExpenseItem(5L))

        assertThat(messages).containsExactly(R.string.error_delete_exchange_rates_unavailable)
        job.cancel()
    }

    @Test
    fun `refused payment delete emits snackbar`() = runTest {
        whenever(repository.deletePaymentById(5L)).thenReturn(TripItemResult.MissingExchangeRate)
        val vm = createViewModel()
        val messages = mutableListOf<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.snackbarEffect.collect { messages.add(it) } }

        vm.onIntent(DeleteItemIntent.DeletePaymentItem(5L))

        assertThat(messages).containsExactly(R.string.error_delete_exchange_rates_unavailable)
        job.cancel()
    }

    @Test
    fun `successful delete emits no snackbar`() = runTest {
        whenever(repository.deleteExpenseById(5L)).thenReturn(TripItemResult.Success)
        val vm = createViewModel()
        val messages = mutableListOf<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.snackbarEffect.collect { messages.add(it) } }

        vm.onIntent(DeleteItemIntent.DeleteExpenseItem(5L))

        assertThat(messages).isEmpty()
        job.cancel()
    }
}
