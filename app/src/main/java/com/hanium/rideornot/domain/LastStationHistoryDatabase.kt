package com.hanium.rideornot.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LastStationHistory::class], version = 1)
abstract class LastStationHistoryDatabase : RoomDatabase() {
    abstract fun lastStationHistoryDao(): LastStationHistoryDao

    companion object {
        private var instance: LastStationHistoryDatabase? = null

        @Synchronized
        fun getInstance(context: Context): LastStationHistoryDatabase? {
            if (instance == null) {
                synchronized(SearchHistoryDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LastStationHistoryDatabase::class.java,
                        "last-station-history-database"
                    ).build()
                }
            }

            return instance
        }
    }

}