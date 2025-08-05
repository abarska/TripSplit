package com.anabars.tripsplit.ui.screens.addexpense

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.model.ExpenseCategory
import java.time.LocalDate

sealed class AddExpenseIntent {
    data class DateSelected(val date: LocalDate) : AddExpenseIntent()
    data class CategoryChanged(val category: ExpenseCategory) : AddExpenseIntent()
    data class AmountChanged(val amount: String) : AddExpenseIntent()
    data class CurrencySelected(val code: String) : AddExpenseIntent()
    data class PayerSelected(val id: Long) : AddExpenseIntent()
    data class ParticipantsSelected(val participants: Set<TripParticipant>) : AddExpenseIntent()
    data object SaveExpense : AddExpenseIntent()
    data object OnBackPressed : AddExpenseIntent()
}