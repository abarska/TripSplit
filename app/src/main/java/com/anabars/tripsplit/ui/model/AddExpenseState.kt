package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import java.time.LocalDate

data class AddExpenseAmountCurrencyState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedCategory: ExpenseCategory = ExpenseCategory.Miscellaneous,
    val tripCurrencies: List<TripCurrency> = emptyList(),
    val expenseAmount: String = "",
    val expenseCurrencyCode: String = "",
    val isError: Boolean = false
)

data class AddExpensePayerParticipantsState (
    val tripParticipants: List<TripParticipant> = emptyList(),
    val expensePayerId: Long = -1L,
    val selectedParticipants: Set<TripParticipant> = emptySet(),
    val isError: Boolean = false
)

sealed class AddExpenseUiEffect {
    data object NavigateBack : AddExpenseUiEffect()
    data class ShowSnackBar(val resId: Int) : AddExpenseUiEffect()
}