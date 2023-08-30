package com.hanium.rideornot.ui.favorite

import com.hanium.rideornot.domain.Favorite

interface IFavoriteRV {
    fun onFavoriteItemClick(favorite: Favorite)
}