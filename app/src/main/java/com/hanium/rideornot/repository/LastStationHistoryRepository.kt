package com.hanium.rideornot.repository

import com.hanium.rideornot.domain.LastStationHistory
import com.hanium.rideornot.domain.LastStationHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LastStationHistoryRepository(private val lastStationHistoryDao: LastStationHistoryDao) {

    suspend fun getAllLastStations(): List<LastStationHistory> {
        return withContext(Dispatchers.IO) {
            lastStationHistoryDao.getAllLastStations()
        }
    }

    suspend fun insertLastStation(lastStationHistory: LastStationHistory) {
        return withContext(Dispatchers.IO) {
            lastStationHistoryDao.insertLastStation(lastStationHistory)
        }
    }

    suspend fun deleteLastStation(lastStationHistory: LastStationHistory) {
        return withContext(Dispatchers.IO) {
            lastStationHistoryDao.deleteLastStation(lastStationHistory)
        }
    }
}