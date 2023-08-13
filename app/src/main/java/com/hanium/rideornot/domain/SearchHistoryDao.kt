package com.hanium.rideornot.domain

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// Dao 인터페이스 정의

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY search_history_id DESC")
    fun getAllData(): LiveData<List<SearchHistory>>

    @Insert
    fun insertData(searchHistory: SearchHistory)

    @Delete
    fun deleteData(searchHistory: SearchHistory)
}