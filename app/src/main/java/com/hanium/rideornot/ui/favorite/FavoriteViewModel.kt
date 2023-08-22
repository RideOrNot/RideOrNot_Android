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

    val favoriteList: LiveData<List<Favorite>>
    private val favoriteRepository: FavoriteRepository
    private val stationDao : StationDao
    private val lineDao : LineDao

    init {
        val favoriteDao = FavoriteDatabase.getInstance(context)!!.favoriteDao()
        favoriteRepository = FavoriteRepository(favoriteDao)
        stationDao = StationDatabase.getInstance(context)!!.stationDao()
        lineDao = StationDatabase.getInstance(context)!!.lineDao()
        favoriteList = favoriteRepository.getAllData
    }

    fun insertFavorite(favorite: Favorite) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingFavorite = favoriteRepository.getFavorite(favorite.stationName)
            if (existingFavorite != null) {
                favoriteRepository.deleteSearchHistory(existingFavorite)
                favoriteRepository.insertSearchHistory(favorite)
            } else {
                favoriteRepository.insertSearchHistory(favorite)
            }
        }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.async(Dispatchers.IO) {
            favoriteRepository.deleteSearchHistory(favorite)
        }
    }

    suspend fun findLinesByStationName(stationName: String) : List<Int> {
        return viewModelScope.async(Dispatchers.IO) {
            return@async stationDao.findLineByName(stationName)
        }.await()
    }

    suspend fun getLineNameByLineId(lineId: Int) : String{
        return viewModelScope.async(Dispatchers.IO) {
            return@async lineDao.getLineNameById(lineId)
        }.await()
    }

}