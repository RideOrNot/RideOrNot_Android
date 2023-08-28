package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.Station
import com.hanium.rideornot.domain.StationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationRepository(private val stationDao: StationDao) {

    suspend fun getAll(): List<Station> {
        return withContext(Dispatchers.IO) {
            stationDao.getAll()
        }
    }

    suspend fun findStationById(stationId: Int): Station {
        return withContext(Dispatchers.IO) {
            stationDao.findStationById(stationId)
        }
    }

    suspend fun findLineByName(stationName: String): List<Int> {
        return withContext(Dispatchers.IO) {
            stationDao.findLineByName(stationName)
        }
    }

    suspend fun findStationByNameAndLineId(stationName: String, lineId: Int): Station {
        return withContext(Dispatchers.IO) {
            stationDao.findStationByNameAndLineId(stationName, lineId)
        }
    }
}