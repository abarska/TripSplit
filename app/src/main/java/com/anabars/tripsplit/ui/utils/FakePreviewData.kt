package com.anabars.tripsplit.ui.utils

import com.anabars.tripsplit.data.room.entity.TripCurrency
import com.anabars.tripsplit.data.room.entity.TripParticipant
import com.anabars.tripsplit.ui.model.AddExpenseUiState
import com.anabars.tripsplit.ui.model.AddTripNameUiState
import com.anabars.tripsplit.ui.model.AddTripParticipantsUiState
import com.anabars.tripsplit.ui.model.AddTripUiState

fun getFakeTripParticipants() = listOf(
    TripParticipant(name = "Harry", multiplicator = 1),
    TripParticipant(name = "Hermione", multiplicator = 2),
    TripParticipant(name = "Ron", multiplicator = 3),
    TripParticipant(name = "Draco", multiplicator = 1)
)

fun getFakeTripCurrencies() = listOf(
    TripCurrency(code = "EUR", tripId = 0),
    TripCurrency(code = "BGN", tripId = 0),
    TripCurrency(code = "RON", tripId = 0),
    TripCurrency(code = "UAH", tripId = 0)
)

fun getFakeAddExpenseUiState() = AddExpenseUiState(
    tripParticipants = getFakeTripParticipants(),
    tripCurrencies = getFakeTripCurrencies(),
    selectedParticipants = getFakeTripParticipants()
        .take(getFakeTripParticipants().size - 1).toSet()
)

fun getFakeTripNameUiState() = AddTripNameUiState(
    tripName = "Placeholder"
)

fun getFakeAddTripUiState() = AddTripUiState(
    tripCurrencies = getFakeTripCurrencies().map { it.code }
)

fun getFakeParticipantsUiState() = AddTripParticipantsUiState(
    tripParticipants = getFakeTripParticipants()
)