package com.anabars.tripsplit.ui.model

import java.math.BigDecimal

data class BalanceDelta(
    val tripId: Long,
    val participantId: Long,
    val deltaUsd: BigDecimal
)
