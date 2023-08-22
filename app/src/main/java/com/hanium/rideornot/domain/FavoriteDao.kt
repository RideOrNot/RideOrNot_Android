package com.hanium.rideornot.domain

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY favorite_id DESC")
    fun getAllData(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE station_name = :stationName")
    suspend fun getDataByStationName(stationName: String): Favorite?

    @Insert
    fun insertData(favorite: Favorite)

    @Delete
    fun deleteData(favorite: Favorite)
}