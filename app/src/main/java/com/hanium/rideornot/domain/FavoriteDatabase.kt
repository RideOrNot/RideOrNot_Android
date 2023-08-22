package com.hanium.rideornot.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private var instance: FavoriteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): FavoriteDatabase? {
            if (instance == null) {
                synchronized(SearchHistoryDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FavoriteDatabase::class.java,
                        "favorite-database"
                    ).build()
                }
            }
            return instance
        }
    }

}