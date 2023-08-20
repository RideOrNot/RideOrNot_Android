package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.LineDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LineRepository(private val lineDao: LineDao) {

    suspend fun getLineNameById(lineId: Int): String {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            lineDao.getLineNameById(lineId)
        }
    }
}