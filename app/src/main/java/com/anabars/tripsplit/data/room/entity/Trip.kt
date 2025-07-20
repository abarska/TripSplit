package com.anabars.tripsplit.data.room.entity

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anabars.tripsplit.R
import com.anabars.tripsplit.common.TripSplitConstants
import java.util.Date

@Entity(tableName = TripSplitConstants.TRIP_TABLE)
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo val title: String = "",
    @ColumnInfo val tag: String = Tag.OTHER.name,
    @ColumnInfo val status: TripStatus = TripStatus.PLANNED,
    @ColumnInfo val ownerId: String = "",
    @ColumnInfo val createdAt: Date = Date()
)

enum class TripStatus(@StringRes val labelRes: Int) {
    PLANNED(R.string.planned),
    STARTED(R.string.started),
    FINISHED(R.string.finished),
    ARCHIVED(R.string.archived);

    companion object {
        fun getInitialTripStatuses() = listOf(PLANNED, STARTED)
    }
}

enum class Tag(@StringRes tagRes: Int) {
    BUSINESS_TRIP(R.string.tag_business_trip),
    VACATION(R.string.tag_vacation),
    HOLIDAY(R.string.tag_holiday),
    WEEK_END(R.string.tag_week_end),
    OTHER(R.string.tag_other)
}