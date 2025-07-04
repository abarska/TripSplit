package com.anabars.tripsplit.ui.model

import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import java.time.LocalDate

data class AddExpenseUiState (
    val tripParticipants: List<TripParticipant> = emptyList(),
    val tripCurrencies: List<TripCurrency> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedCategory: ExpenseCategory = ExpenseCategory.Miscellaneous,
    val expenseAmount: String = "",
    val expenseCurrencyCode: String = "",
    val expensePayerId: Long = -1L,
    val selectedParticipants: Set<TripParticipant> = emptySet()
)