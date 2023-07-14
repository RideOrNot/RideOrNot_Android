package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "station_exit", indices = [Index("exit_id")])
data class StationExit(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "exit_id")
    var exitId: Int = 0,
    @ColumnInfo(name = "exit_name")
    var exitName: String = "",
    @ColumnInfo(name = "statn_id")
    var stationId: Int = 0,
    @ColumnInfo(name = "exit_latitude")
    var stationLatitude: Double = 0.0,
    @ColumnInfo(name = "exit_longitude")
    var stationLongitude: Double = 0.0
)
