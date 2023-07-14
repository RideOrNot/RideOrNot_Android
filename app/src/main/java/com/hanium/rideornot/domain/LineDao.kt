package com.hanium.rideornot.domain

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LineDao {
    @Query("SELECT * FROM line WHERE line_id IN (:lineIds)")
    fun getLinesByIds(lineIds: List<Int>): List<Line>
}