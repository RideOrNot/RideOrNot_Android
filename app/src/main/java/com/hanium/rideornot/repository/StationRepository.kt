package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.domain.StationDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationRepository(private val stationDao: StationDao) {

    suspend fun getAll() : List<Station> {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationDao.getAll()
        }
    }

    suspend fun findStationById(stationId: Int) : Station {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationDao.findStationById(stationId)
        }
    }

    suspend fun findLineByName(stationName: String) : List<Int> {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationDao.findLineByName(stationName)
        }
    }

    suspend fun findNeighboringStation(stationName: String, lineId: Int) : Station {
        return withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            stationDao.findNeighboringStation(stationName, lineId)
        }
    }
}