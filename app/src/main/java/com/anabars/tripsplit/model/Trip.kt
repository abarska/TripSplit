package com.anabars.tripsplit.model

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anabars.tripsplit.R
import java.util.Date
import java.util.UUID

@Entity(tableName = "trip_table")
data class Trip(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val tag: String = Tag.OTHER.name,
    @ColumnInfo val status: TripStatus = TripStatus.PLANNED,
    @ColumnInfo val ownerId: String = "",
//    @ColumnInfo val participantIds: List<String> = emptyList(),
    @ColumnInfo val createdAt: Date = Date()
)

enum class TripStatus {
    PLANNED,
    STARTED,
    FINISHED,
    COMPLETED
}

enum class Tag(@StringRes tagRes: Int) {
    BUSINESS_TRIP(R.string.tag_business_trip),
    VACATION(R.string.tag_vacation),
    HOLIDAY(R.string.tag_holiday),
    WEEK_END(R.string.tag_week_end),
    OTHER(R.string.tag_other)
}