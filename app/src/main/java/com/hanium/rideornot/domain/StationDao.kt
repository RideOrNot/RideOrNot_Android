package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StationDao {

    @Query("SELECT * FROM station")
    fun getAll(): List<Station>

    @Query("SELECT line_id FROM station WHERE statn_name LIKE :stationName")
    fun findLineByName(stationName: String): List<Int>

}