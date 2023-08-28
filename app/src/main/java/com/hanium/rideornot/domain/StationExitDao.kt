package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StationExitDao {

    @Query("SELECT * FROM stationExit")
    suspend fun getAll(): List<StationExit>

    @Query("SELECT * FROM stationExit WHERE exit_id = :exitId")
    suspend fun findStationExitById(exitId: Int): StationExit
}