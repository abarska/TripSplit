package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.repository.TripRepository
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class TripOverviewViewModelTest {

    private val repository: TripRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun expense(category: ExpenseCategory, currency: String, amountMinor: Long, tripAmountMinor: Long) =
        TripExpense(
            tripId = 1L,
            paidById = 1L,
            category = category,
            timestamp = 0L,
            amountMinor = amountMinor,
            currency = currency,
            rateToTrip = BigDecimal.ONE,
            tripAmountMinor = tripAmountMinor
        )

    private fun createViewModel(tripCurrency: String, expenses: List<TripExpense>): TripOverviewViewModel {
        whenever(repository.getExpensesByTripId(1L)).thenReturn(flowOf(expenses))
        whenever(repository.getTripDetailsFlow(1L)).thenReturn(
            flowOf(TripDetails(Trip(id = 1L, defaultCurrency = tripCurrency), emptyList(), emptyList()))
        )
        return TripOverviewViewModel(repository, SavedStateHandle(mapOf("tripId" to 1L)))
    }

    @Test
    fun `sums frozen trip amounts per category without any exchange rates`() = runTest {
        val viewModel = createViewModel(
            "EUR",
            listOf(
                expense(ExpenseCategory.Accommodation, "EUR", 5000, 5000),
                expense(ExpenseCategory.Accommodation, "USD", 10000, 9137),
                expense(ExpenseCategory.Transportation, "EUR", 1250, 1250)
            )
        )

        val result = viewModel.expenseCategorizationResult
            .first { it is ExpenseCategorizationResult.Success } as ExpenseCategorizationResult.Success

        assertThat(result.data[ExpenseCategory.Accommodation]).isEqualTo(141.37)
        assertThat(result.data[ExpenseCategory.Transportation]).isEqualTo(12.50)
    }

    @Test
    fun `scales totals by the trip currency's fraction digits`() = runTest {
        val viewModel = createViewModel(
            "JPY",
            listOf(expense(ExpenseCategory.Food, "JPY", 1500, 1500))
        )

        val result = viewModel.expenseCategorizationResult
            .first { it is ExpenseCategorizationResult.Success } as ExpenseCategorizationResult.Success

        assertThat(result.data[ExpenseCategory.Food]).isEqualTo(1500.0)
    }
}
