package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import java.time.LocalDate

data class AddExpenseDateCategoryState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedCategory: ExpenseCategory = ExpenseCategory.Miscellaneous
)

data class AddExpenseAmountCurrencyState(
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
    object NavigateBack : AddExpenseUiEffect()
    data class ShowSnackBar(val resId: Int) : AddExpenseUiEffect()
}

sealed class AddExpenseEvent {
    data class DateSelected(val date: LocalDate) : AddExpenseEvent()
    data class CategoryChanged(val category: ExpenseCategory) : AddExpenseEvent()
    data class AmountChanged(val amount: String) : AddExpenseEvent()
    data class CurrencySelected(val code: String) : AddExpenseEvent()
    data class PayerSelected(val id: Long) : AddExpenseEvent()
    data class ParticipantsSelected(val participants: Set<TripParticipant>) : AddExpenseEvent()
    object SaveExpense : AddExpenseEvent()
    object OnBackPressed : AddExpenseEvent()
}