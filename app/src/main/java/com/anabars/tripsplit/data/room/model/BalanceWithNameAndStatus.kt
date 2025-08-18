package com.anabars.tripsplit.data.room.model

import com.anabars.tripsplit.data.room.entity.ParticipantStatus
import java.math.BigDecimal

data class BalanceWithNameAndStatus(
    val participantName: String,
    val participantStatus: ParticipantStatus,
    val amountUsd: BigDecimal
)