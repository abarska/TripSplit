package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants
import com.anabars.tripsplit.data.room.entity.ParticipantBalance
import com.anabars.tripsplit.data.room.model.BalanceWithNameAndStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface BalanceDao {
    @Query("SELECT * FROM ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} WHERE tripId = :tripId")
    fun getBalancesForTrip(tripId: Long): Flow<List<ParticipantBalance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBalance(balance: ParticipantBalance)

    @Query(
        "UPDATE ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} " +
                "SET amountUsd = amountUsd + :deltaUsd " +
                "WHERE tripId = :tripId AND participantId = :participantId"
    )
    suspend fun updateBalance(tripId: Long, participantId: Long, deltaUsd: BigDecimal): Int

    @Query(
        "INSERT INTO ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} " +
                "(tripId, participantId, amountUsd) VALUES (:tripId, :participantId, :deltaUsd)"
    )
    suspend fun insertBalance(tripId: Long, participantId: Long, deltaUsd: BigDecimal)

    @Query(
        """
        SELECT tp.name AS participantName,
               tp.status AS participantStatus,
               pb.amountUsd AS amount,
               '${TripSplitConstants.BASE_CURRENCY}' AS amountCurrency
        FROM ${TripSplitConstants.PARTICIPANT_BALANCES_TABLE} pb
        INNER JOIN ${TripSplitConstants.TRIP_PARTICIPANTS_TABLE} tp
               ON pb.participantId = tp.id
        WHERE pb.tripId = :tripId
        """
    )
    fun getBalancesWithNameAndStatus(tripId: Long): Flow<List<BalanceWithNameAndStatus>>
}