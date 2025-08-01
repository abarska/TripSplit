package com.anabars.tripsplit.ui.utils

import com.anabars.tripsplit.data.room.entity.Trip
import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripExpense
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.data.room.entity.TripSettlement
import com.anabars.tripsplit.data.room.model.TripDetails
import com.anabars.tripsplit.ui.model.AddExpenseAmountCurrencyState
import com.anabars.tripsplit.ui.model.AddExpensePayerParticipantsState
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.model.AddTripParticipantsUiState
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.viewmodels.ExpenseCategorizationResult

fun getFakeTripParticipants() = listOf(
    TripParticipant(id = 1, name = "Harry", multiplicator = 1),
    TripParticipant(id = 2, name = "Hermione", multiplicator = 2),
    TripParticipant(id = 3, name = "Ron", multiplicator = 3),
    TripParticipant(id = 4, name = "Draco", multiplicator = 1)
)

fun getFakeTripCurrencies() = listOf(
    TripCurrency(code = "EUR", tripId = 0),
    TripCurrency(code = "BGN", tripId = 0),
    TripCurrency(code = "RON", tripId = 0),
    TripCurrency(code = "UAH", tripId = 0)
)

fun getFakeTripNameUiState() = AddTripNameUiState(
    tripName = "Placeholder"
)

fun getFakeParticipantsUiState() = AddTripParticipantsUiState(
    tripParticipants = getFakeTripParticipants()
)

fun getFakeAmountCurrencyUiState() = AddExpenseAmountCurrencyState(
    tripCurrencies = getFakeTripCurrencies()
)

fun getFakeAmountCurrencyUiStateWithError() = AddExpenseAmountCurrencyState(
    tripCurrencies = getFakeTripCurrencies(),
    isError = true
)

fun getFakePayerParticipantsState() = AddExpensePayerParticipantsState(
    tripParticipants = getFakeTripParticipants(),
    selectedParticipants = getFakeTripParticipants()
        .take(getFakeTripParticipants().size - 1).toSet()
)

fun getFakeTripExpense() = TripExpense(
    paidById = 1,
    amount = 50.0,
    currency = "EUR",
    category = ExpenseCategory.Accommodation,
    timestamp = System.currentTimeMillis(),
    tripId = 0
)

fun getFakeAddExpensePayerParticipantsState() = AddExpensePayerParticipantsState(
    tripParticipants = getFakeTripParticipants(),
    expensePayerId = 1,
    selectedParticipants = getFakeTripParticipants().toSet()
)

fun getFakeAddExpensePayerParticipantsStateWithError() = AddExpensePayerParticipantsState(
    tripParticipants = getFakeTripParticipants(),
    expensePayerId = 1,
    selectedParticipants = getFakeTripParticipants().toSet(),
    isError = true
)

fun getFakePieChartData(): MutableMap<ExpenseCategory, Double> {
    val data = mutableMapOf<ExpenseCategory, Double>()
    val categories = ExpenseCategory.allExpenseCategories()
    categories.forEachIndexed { index, category ->
        data[categories[index]] = 1.00 + (index * 1.00)
    }
    return data
}

fun getFakeTripWithDetails() = TripDetails(
    trip = Trip(id = 0, title = "Antarctica"),
    participants = getFakeTripParticipants(),
    currencies = getFakeTripCurrencies()
)

fun getFakeExpenseCategorizationResultUnavailableData() =
    ExpenseCategorizationResult.UnavailableData

fun getFakeExpenseCategorizationResultMissingCurrencies() =
    ExpenseCategorizationResult.MissingCurrencies(getFakeTripCurrencies().map { it.code })

fun getFakeExpenseCategorizationResultInsufficientData() =
    ExpenseCategorizationResult.Success(
        data = getFakePieChartData()
            .entries
            .take(2)
            .associate { it.toPair() })

fun getFakeExpenseCategorizationResultSuccess() =
    ExpenseCategorizationResult.Success(getFakePieChartData())

fun getFakeSettlement() = TripSettlement(
    id = 1L,
    tripId = 2L,
    fromUserId = 1L,
    toUserId = 2L,
    amount = 30.00,
    currency = "EUR",
    timestamp = System.currentTimeMillis()
)