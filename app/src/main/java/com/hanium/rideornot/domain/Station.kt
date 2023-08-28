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
    var lineId: Int = 0,

    @ColumnInfo(name = "before_station1")
    var beforeStation1: String? = "",
    @ColumnInfo(name = "before_station2")
    var beforeStation2: String? = "",
    @ColumnInfo(name = "next_station1")
    var nextStation1: String? = "",
    @ColumnInfo(name = "next_station2")
    var nextStation2: String? = "",
    @ColumnInfo(name = "before_station_id1")
    var beforeStationId1: Int = 0,
    @ColumnInfo(name = "before_station_id2")
    var beforeStationId2: Int = 0,
    @ColumnInfo(name = "next_station_id1")
    var nextStationId1: Int = 0,
    @ColumnInfo(name = "next_station_id2")
    var nextStationId2: Int = 0
)
