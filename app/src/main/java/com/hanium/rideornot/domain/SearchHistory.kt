package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "search_history_id")
    val searchHistoryId: Long = 0,

    @ColumnInfo(name = "station_id")
    val stationId: Int,

    @ColumnInfo(name = "station_name")
    val stationName: String
)


