package com.example.marysyatravel.data

data class Participant(
    val userId: String = "",
    val name: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)