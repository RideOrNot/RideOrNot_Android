package com.hanium.rideornot.repository

import androidx.lifecycle.LiveData
import com.hanium.rideornot.domain.Favorite
import com.hanium.rideornot.domain.FavoriteDao
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

    suspend fun getLastOrder() : Int {0
        return withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
            favoriteDao.getLastOrder() ?: 0
        }
    }

    suspend fun updateFavorite(favorite: Favorite) {
        favoriteDao.updateData(favorite)
    }

    fun insertFavorite(favorite: Favorite) {
        favoriteDao.insertData(favorite)
    }

    fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteData(favorite)
    }
}

