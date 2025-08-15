package com.anabars.tripsplit.ui.screens.additem

import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.model.ExpenseCategory
import java.time.LocalDate

sealed class AddItemIntent {
    data class DateSelected(val date: LocalDate) : AddItemIntent()
    data class CategoryChanged(val category: ExpenseCategory) : AddItemIntent()
    data class AmountChanged(val amount: String) : AddItemIntent()
    data class CurrencySelected(val code: String) : AddItemIntent()
    data class PayerSelected(val id: Long) : AddItemIntent()
    data class ParticipantsSelected(val participants: Set<TripParticipant>) : AddItemIntent()
    data object SaveItem : AddItemIntent()
    data object OnBackPressed : AddItemIntent()
}