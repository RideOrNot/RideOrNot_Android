package com.hanium.rideornot.domain

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY favorite_id DESC")
    fun getAllData(): LiveData<MutableList<Favorite>>

    @Query("SELECT * FROM favorite WHERE station_name = :stationName")
    suspend fun getDataByStationName(stationName: String): Favorite?

    @Query("SELECT MAX(order_number) FROM favorite")
    suspend fun getLastOrder(): Int?

    @Insert
    fun insertData(favorite: Favorite)

    @Delete
    fun deleteData(favorite: Favorite)

    @Update
    fun updateData(favorite: Favorite)
}