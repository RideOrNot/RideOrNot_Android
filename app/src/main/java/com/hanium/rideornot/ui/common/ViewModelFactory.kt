package com.hanium.rideornot.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.repository.ArrivalRepository
import com.hanium.rideornot.source.ArrivalRemoteDataSourceImpl
import com.hanium.rideornot.ui.search.SearchViewModel
import com.hanium.rideornot.ui.StationDetailViewModel
import com.hanium.rideornot.ui.favorite.FavoriteViewModel
import com.hanium.rideornot.ui.home.HomeViewModel
import com.hanium.rideornot.utils.NetworkModule.getArrivalService

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StationDetailViewModel::class.java)) {
            val repository = ArrivalRepository(ArrivalRemoteDataSourceImpl(getArrivalService()))
            StationDetailViewModel(context, repository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val repository = ArrivalRepository(ArrivalRemoteDataSourceImpl(getArrivalService()))
            HomeViewModel(context, repository) as T
        } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            SearchViewModel(context) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(context) as T
        } else {
            throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
        }
    }
}