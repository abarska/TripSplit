package com.anabars.tripsplit.model

data class Participant(
    val userId: String = "",
    val name: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)