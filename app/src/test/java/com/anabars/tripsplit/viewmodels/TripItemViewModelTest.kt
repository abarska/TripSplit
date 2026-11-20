package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.repository.TripItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
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
    fun `expense delete intent deletes the expense`() = runTest {
        createViewModel().onIntent(DeleteItemIntent.DeleteExpenseItem(5L))

        verify(repository).deleteExpenseById(5L)
    }

    @Test
    fun `payment delete intent deletes the payment`() = runTest {
        createViewModel().onIntent(DeleteItemIntent.DeletePaymentItem(5L))

        verify(repository).deletePaymentById(5L)
    }
}
