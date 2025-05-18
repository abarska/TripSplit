package com.example.marysyatravel.data

data class Trip(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val tag: String = "",
    val status: TripStatus = TripStatus.PLANNED,
    val ownerId: String = "",
    val participantIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class TripStatus {
    PLANNED,
    STARTED,
    FINISHED,
    COMPLETED
}