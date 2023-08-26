package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StationDao {

    @Query("SELECT * FROM station")
    suspend fun getAll(): List<Station>

    @Query("SELECT * FROM station WHERE station_id = :stationId")
    suspend fun findStationById(stationId: Int): Station

    @Query("SELECT line_id FROM station WHERE statn_name = :stationName")
    suspend fun findLineByName(stationName: String): List<Int>

    @Query("SELECT * FROM station WHERE statn_name = :stationName")
    suspend fun findStationByName(stationName: String): Station

    @Query("SELECT * FROM station WHERE statn_name LIKE '%' || :searchQuery || '%'")
    suspend fun findStationsByName(searchQuery: String): List<Station>

    @Query("SELECT * FROM station WHERE statn_name = :stationName AND line_id = :lineId")
    suspend fun findStationByNameAndLineId(stationName: String, lineId: Int): Station

}