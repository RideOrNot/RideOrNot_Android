package com.hanium.rideornot.ui.favorite

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanium.rideornot.domain.*
import com.hanium.rideornot.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class FavoriteViewModel(context: Context) : ViewModel() {

    val favoriteList: LiveData<MutableList<Favorite>>
    private val favoriteRepository: FavoriteRepository
    private val stationDao: StationDao
    private val lineDao: LineDao

    init {
        val favoriteDao = FavoriteDatabase.getInstance(context)!!.favoriteDao()
        favoriteRepository = FavoriteRepository(favoriteDao)
        stationDao = StationDatabase.getInstance(context)!!.stationDao()
        lineDao = StationDatabase.getInstance(context)!!.lineDao()
        favoriteList = favoriteRepository.getAllData
    }

    fun updateOrder(itemList: List<Favorite>) {
        viewModelScope.launch(Dispatchers.IO) {
            val difference = favoriteList.value!!.filterNot {it in itemList}
            for ((index, item) in itemList.withIndex()) {
                item.orderNumber = index
                favoriteRepository.updateFavorite(item)
            }
            for (item in difference) {
                favoriteRepository.deleteFavorite(item)
            }
        }
    }

    fun insertFavorite(stationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingFavorite = favoriteRepository.getFavorite(stationName)
            if (existingFavorite != null) {
                favoriteRepository.deleteFavorite(existingFavorite)
            }
            val lastOrder = favoriteRepository.getLastOrder()
            favoriteRepository.insertFavorite(Favorite(
                stationName = stationName,
                orderNumber = lastOrder + 1
            ))
        }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.async(Dispatchers.IO) {
            favoriteRepository.deleteFavorite(favorite)
        }
    }

    suspend fun getFavorite(stationName: String): Favorite? {
        return favoriteRepository.getFavorite(stationName)
    }

    suspend fun findLinesByStationName(stationName: String): List<Int> {
        return viewModelScope.async(Dispatchers.IO) {
            return@async stationDao.findLineByName(stationName)
        }.await()
    }

    suspend fun getLineNameByLineId(lineId: Int): String {
        return viewModelScope.async(Dispatchers.IO) {
            return@async lineDao.getLineNameById(lineId)
        }.await()
    }

}