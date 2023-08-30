package com.hanium.rideornot.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "favorite_id")
    val favoriteId: Long = 0,

    @ColumnInfo(name = "station_name")
    val stationName: String,

    @ColumnInfo(name = "order_number")
    var orderNumber: Int
//
//    @ColumnInfo(name = "is_expanded")
//    val isExpanded: Boolean,
//
//    @ColumnInfo(name = "current_line_index")
//    val currentLineIndex: Int
)