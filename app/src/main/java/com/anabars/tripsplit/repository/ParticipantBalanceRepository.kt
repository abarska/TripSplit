package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.ParticipantBalanceDao
import javax.inject.Inject

class ParticipantBalanceRepository @Inject constructor(
    private val participantBalanceDao: ParticipantBalanceDao,
) {}