package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "line", indices = [Index("line_id")])
data class Line(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "line_id")
    var lineId: Int = 0,
    @ColumnInfo(name = "line_name")
    var lineName: String = "",
    @ColumnInfo(name = "csv_line")
    var csvLine: Int = 0
)