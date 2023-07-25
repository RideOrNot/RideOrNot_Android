package com.hanium.rideornot.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hanium.rideornot.repository.ArrivalRepository
import com.hanium.rideornot.source.ArrivalRemoteDataSourceImpl
import com.hanium.rideornot.ui.StationDetailViewModel
import com.hanium.rideornot.utils.NetworkModule.getArrivalService

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        if (modelClass.isAssignableFrom(StationDetailViewModel::class.java)) {
            val repository = ArrivalRepository(ArrivalRemoteDataSourceImpl(getArrivalService()))
            return StationDetailViewModel(context, repository) as T
        } else {
            throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
        }
    }
}