package com.anabars.tripsplit.model

data class Expense(
    val id: String = "",
    val paidBy: String = "",
    val sharedWith: List<String> = emptyList(),
    val amount: Double = 0.0,
    val currency: String = "EUR",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)