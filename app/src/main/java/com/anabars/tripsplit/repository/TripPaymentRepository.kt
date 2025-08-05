package com.anabars.tripsplit.repository

import com.anabars.tripsplit.data.room.dao.TripDao
import com.anabars.tripsplit.data.room.dao.TripPaymentDao
import javax.inject.Inject

class TripPaymentRepository@Inject constructor(
    private val tripPaymentDao: TripPaymentDao,
    private val tripDao: TripDao
) {
}