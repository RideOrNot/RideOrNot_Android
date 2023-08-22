package com.hanium.rideornot.repository

import androidx.lifecycle.LiveData
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.domain.FavoriteDao
import com.hanium.rideornot.domain.SearchHistory
import com.hanium.rideornot.domain.SearchHistoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    val getAllData: LiveData<List<Favorite>> = favoriteDao.getAllData()

    suspend fun getFavorite(stationName: String) : Favorite? {
        return withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
            favoriteDao.getDataByStationName(stationName)
        }
    }

    fun insertSearchHistory(favorite: Favorite) {
        favoriteDao.insertData(favorite)
    }

    fun deleteSearchHistory(favorite: Favorite) {
        favoriteDao.deleteData(favorite)
    }
}

