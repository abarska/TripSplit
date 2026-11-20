package com.anabars.tripsplit.repository

sealed interface TripItemResult {
    data object Success : TripItemResult
    data object MissingExchangeRate : TripItemResult
}
