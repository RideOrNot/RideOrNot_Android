package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.StationExit
import com.hanium.rideornot.domain.StationExitDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationExitRepository (private val stationExitDao: StationExitDao) {

    suspend fun getAll() : List<StationExit> {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationExitDao.getAll()
        }
    }

    suspend fun findStationExitById(exitId: Int) : StationExit {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationExitDao.findStationExitById(exitId)
        }
    }

}