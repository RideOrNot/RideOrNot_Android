package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_station_history")
data class LastStationHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "last_history_id")
    val lastStationHistoryId: Int = 0,

    @ColumnInfo(name = "statn_name")
    val stationName: String
)
