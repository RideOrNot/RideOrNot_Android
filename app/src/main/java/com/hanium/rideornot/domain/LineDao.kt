package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LineDao {
    @Query("SELECT * FROM line WHERE line_id IN (:lineIds)")
    suspend fun getLinesByIds(lineIds: List<Int>): List<Line>

    @Query("SELECT line_name FROM line WHERE line_id = :lineId")
    suspend fun getLineNameById(lineId: Int): String
}