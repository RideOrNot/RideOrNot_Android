package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "station", indices = [Index("station_id")])
data class Station(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "station_id")
    var stationId: Int = 0,
    @ColumnInfo(name = "statn_latitude")
    var stationLatitude: Double = 0.0,
    @ColumnInfo(name = "statn_longitude")
    var stationLongitude: Double = 0.0,
    @ColumnInfo(name = "statn_name")
    var stationName: String = "",
    @ColumnInfo(name = "line_id")
    var lineId: Int = 0
)
