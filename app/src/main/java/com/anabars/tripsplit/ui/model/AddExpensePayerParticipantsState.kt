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
)

data class AddExpensePayerParticipantsState (
    val tripParticipants: List<TripParticipant> = emptyList(),
    val expensePayerId: Long = -1L,
    val selectedParticipants: Set<TripParticipant> = emptySet()
)

enum class AddExpenseErrorState {
    EXPENSE_AMOUNT, SELECTED_PARTICIPANTS, NONE
}

sealed class AddExpenseEvent {
    data class DateSelected(val date: LocalDate) : AddExpenseEvent()
    data class CategoryChanged(val category: ExpenseCategory) : AddExpenseEvent()
    data class AmountChanged(val amount: String) : AddExpenseEvent()
    data class CurrencySelected(val code: String) : AddExpenseEvent()
    data class PayerSelected(val id: Long) : AddExpenseEvent()
    data class ParticipantsSelected(val participants: Set<TripParticipant>) : AddExpenseEvent()
}