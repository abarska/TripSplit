package com.anabars.tripsplit.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.anabars.tripsplit.R
import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.repository.TripItemRepository
import com.anabars.tripsplit.repository.TripItemResult
import com.anabars.tripsplit.ui.model.AddItemUiEffect
import com.anabars.tripsplit.ui.screens.additem.AddItemIntent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CompletableDeferred
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
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {

    private val repository: TripItemRepository = mock()
    private val payer = TripParticipant(id = 1L, name = "A", tripId = 1L)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val removed = TripParticipant(id = 2L, name = "B", tripId = 1L, status = ParticipantStatus.INACTIVE)

    private suspend fun createViewModel(
        useCase: AddItemViewModel.UseCase,
        defaultCurrency: String = "EUR"
    ): AddItemViewModel {
        whenever(repository.getTripDefaultCurrency(1L)).thenReturn(defaultCurrency)
        whenever(repository.getActiveParticipantsByTripId(1L)).thenReturn(flowOf(listOf(payer)))
        whenever(repository.getParticipantsByTripId(1L)).thenReturn(flowOf(listOf(payer, removed)))
        whenever(repository.getActiveCurrenciesByTripId(1L)).thenReturn(flowOf(emptyList()))
        return AddItemViewModel(
            SavedStateHandle(mapOf("tripId" to 1L, "useCase" to useCase.name)),
            repository
        )
    }

    private suspend fun fillForm(vm: AddItemViewModel) {
        vm.onIntent(AddItemIntent.AmountChanged("10"))
        vm.onIntent(AddItemIntent.PayerSelected(payer.id))
        vm.onIntent(AddItemIntent.ParticipantsSelected(setOf(payer)))
    }

    @Test
    fun `saving expense with missing rate shows snackbar and does not navigate back`() = runTest {
        whenever(repository.saveExpenseWithParticipants(any(), any()))
            .thenReturn(TripItemResult.MissingExchangeRate)
        val vm = createViewModel(AddItemViewModel.UseCase.EXPENSE)
        val effects = mutableListOf<AddItemUiEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiEffect.collect { effects.add(it) } }

        fillForm(vm)
        vm.onIntent(AddItemIntent.SaveItem)

        assertThat(effects)
            .containsExactly(AddItemUiEffect.ShowSnackBar(R.string.error_save_exchange_rates_unavailable))
        job.cancel()
    }

    @Test
    fun `saving payment with missing rate shows snackbar and does not navigate back`() = runTest {
        whenever(repository.savePayment(any())).thenReturn(TripItemResult.MissingExchangeRate)
        val vm = createViewModel(AddItemViewModel.UseCase.PAYMENT)
        val effects = mutableListOf<AddItemUiEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiEffect.collect { effects.add(it) } }

        fillForm(vm)
        vm.onIntent(AddItemIntent.SaveItem)

        assertThat(effects)
            .containsExactly(AddItemUiEffect.ShowSnackBar(R.string.error_save_exchange_rates_unavailable))
        job.cancel()
    }

    @Test
    fun `successful save navigates back`() = runTest {
        whenever(repository.savePayment(any())).thenReturn(TripItemResult.Success)
        val vm = createViewModel(AddItemViewModel.UseCase.PAYMENT)
        val effects = mutableListOf<AddItemUiEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiEffect.collect { effects.add(it) } }

        fillForm(vm)
        vm.onIntent(AddItemIntent.SaveItem)

        assertThat(effects).containsExactly(AddItemUiEffect.NavigateBack(false))
        job.cancel()
    }

    @Test
    fun `second save tap while saving does not save twice`() = runTest {
        val pending = CompletableDeferred<TripItemResult>()
        whenever(repository.savePayment(any())).doSuspendableAnswer { pending.await() }
        val vm = createViewModel(AddItemViewModel.UseCase.PAYMENT)

        fillForm(vm)
        vm.onIntent(AddItemIntent.SaveItem)
        vm.onIntent(AddItemIntent.SaveItem)
        pending.complete(TripItemResult.Success)

        verify(repository, times(1)).savePayment(any())
    }

    @Test
    fun `save can be retried after missing rate`() = runTest {
        whenever(repository.savePayment(any())).thenReturn(TripItemResult.MissingExchangeRate)
        val vm = createViewModel(AddItemViewModel.UseCase.PAYMENT)

        fillForm(vm)
        vm.onIntent(AddItemIntent.SaveItem)
        vm.onIntent(AddItemIntent.SaveItem)

        verify(repository, times(2)).savePayment(any())
    }

    @Test
    fun `currency is preselected to the trip default currency`() = runTest {
        val vm = createViewModel(AddItemViewModel.UseCase.EXPENSE, defaultCurrency = "GBP")

        assertThat(vm.amountCurrencyState.value.expenseCurrencyCode).isEqualTo("GBP")
    }

    @Test
    fun `too many decimals is rejected with the currency's limit and nothing is saved`() = runTest {
        val vm = createViewModel(AddItemViewModel.UseCase.EXPENSE)
        val effects = mutableListOf<AddItemUiEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiEffect.collect { effects.add(it) } }

        fillForm(vm)
        vm.onIntent(AddItemIntent.AmountChanged("12.345"))
        vm.onIntent(AddItemIntent.SaveItem)

        assertThat(vm.amountCurrencyState.value.maxDecimals).isEqualTo(2)
        assertThat(vm.amountCurrencyState.value.isError).isTrue()
        assertThat(effects).containsExactly(AddItemUiEffect.ShowSnackBar(R.string.error_amount_invalid))
        verify(repository, times(0)).saveExpenseWithParticipants(any(), any())
        job.cancel()
    }

    @Test
    fun `plain invalid amount does not report a decimals limit`() = runTest {
        val vm = createViewModel(AddItemViewModel.UseCase.EXPENSE)

        fillForm(vm)
        vm.onIntent(AddItemIntent.AmountChanged("0"))
        vm.onIntent(AddItemIntent.SaveItem)

        assertThat(vm.amountCurrencyState.value.maxDecimals).isNull()
    }

    @Test
    fun `expense offers only active participants`() = runTest {
        val vm = createViewModel(AddItemViewModel.UseCase.EXPENSE)

        assertThat(vm.payerParticipantsState.value.tripParticipants).containsExactly(payer)
    }

    @Test
    fun `payment offers removed participants too`() = runTest {
        val vm = createViewModel(AddItemViewModel.UseCase.PAYMENT)

        assertThat(vm.payerParticipantsState.value.tripParticipants)
            .containsExactly(payer, removed).inOrder()
    }
}
