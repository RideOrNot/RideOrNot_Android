package com.hanium.rideornot.domain

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY search_history_id DESC")
    fun getAllData(): LiveData<List<SearchHistory>>

    @Query("SELECT * FROM search_history WHERE station_name = :stationName")
    suspend fun getDataByStationName(stationName: String): SearchHistory?

    @Insert
    fun insertData(searchHistory: SearchHistory)

    @Delete
    fun deleteData(searchHistory: SearchHistory)
}