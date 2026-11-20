package com.anabars.tripsplit.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.anabars.tripsplit.common.TripSplitConstants.EXPENSE_SHARES_TABLE
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_EXPENSES_TABLE
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_PARTICIPANTS_TABLE
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_PAYMENTS_TABLE
import com.anabars.tripsplit.common.TripSplitConstants.TRIP_TABLE
import com.anabars.tripsplit.data.room.model.ParticipantBalanceRow
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Query(
        """
        SELECT p.id AS participantId, p.name AS name, p.status AS status,
               COALESCE(SUM(m.delta), 0) AS balanceMinor
        FROM $TRIP_PARTICIPANTS_TABLE p
        LEFT JOIN (
            SELECT paidById AS participantId, tripAmountMinor AS delta FROM $TRIP_EXPENSES_TABLE
            UNION ALL
            SELECT participantId, -shareMinor FROM $EXPENSE_SHARES_TABLE
            UNION ALL
            SELECT fromUserId, tripAmountMinor FROM $TRIP_PAYMENTS_TABLE
            UNION ALL
            SELECT toUserId, -tripAmountMinor FROM $TRIP_PAYMENTS_TABLE
        ) m ON m.participantId = p.id
        WHERE p.tripId = :tripId
        GROUP BY p.id
        ORDER BY p.id
        """
    )
    fun getBalances(tripId: Long): Flow<List<ParticipantBalanceRow>>

    @Query("SELECT defaultCurrency FROM $TRIP_TABLE WHERE id = :tripId")
    fun getTripCurrency(tripId: Long): Flow<String?>
}
