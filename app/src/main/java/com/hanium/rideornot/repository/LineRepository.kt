package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.domain.LineDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LineRepository(private val lineDao: LineDao) {

    suspend fun getLinesByIds(lineIds: List<Int>): List<Line> {
        return withContext(Dispatchers.IO) {
            lineDao.getLinesByIds(lineIds)
        }
    }

    suspend fun getLineNameById(lineId: Int): String {
        return withContext(Dispatchers.IO) {
            lineDao.getLineNameById(lineId)
        }
    }
}