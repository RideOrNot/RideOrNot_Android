package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LastStationHistoryDao {
    @Query("SELECT * FROM last_station_history")
    suspend fun getAllLastStations(): List<LastStationHistory>

    @Query("SELECT * FROM last_station_history WHERE statn_name = :stationName")
    suspend fun getLastStationsByStationName(stationName: String): LastStationHistory?

    @Insert
    suspend fun insertLastStation(lastStationHistory: LastStationHistory)

    @Delete
    suspend fun deleteLastStation(lastStationHistory: LastStationHistory)
}