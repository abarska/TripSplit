package com.anabars.tripsplit.model

data class Settlement(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val amount: Double = 0.0,
    val currency: String = "EUR",
    val status: SettlementStatus = SettlementStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SettlementStatus {
    PENDING,
    CONFIRMED
}