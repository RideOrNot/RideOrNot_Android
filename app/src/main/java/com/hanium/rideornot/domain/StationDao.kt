package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StationDao {

    @Query("SELECT * FROM station")
    fun getAll(): List<Station>


    /** 해당 호선의 line_id 목록을 가져옴 */
    @Query("SELECT line_id FROM station WHERE statn_name LIKE :stationName")
    fun findLineByName(stationName: String): List<Int>

    @Query("SELECT * FROM station WHERE statn_name LIKE '%' || :searchQuery || '%'")
    suspend fun findStationsByName(searchQuery: String): List<Station>

}