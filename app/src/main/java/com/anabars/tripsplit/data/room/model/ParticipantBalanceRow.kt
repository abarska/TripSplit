package com.anabars.tripsplit.data.room.model

import com.anabars.tripsplit.data.room.entity.ParticipantStatus

data class ParticipantBalanceRow(
    val participantId: Long,
    val name: String,
    val status: ParticipantStatus,
    val balanceMinor: Long
)
