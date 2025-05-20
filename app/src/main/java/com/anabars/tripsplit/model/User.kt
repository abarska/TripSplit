package com.anabars.tripsplit.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val preferredCurrency: String = "EUR",
)