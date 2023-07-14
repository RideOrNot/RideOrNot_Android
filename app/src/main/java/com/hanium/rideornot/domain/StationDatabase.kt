package com.hanium.rideornot.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Line::class, Station::class, StationExit::class], version = 1)
abstract class StationDatabase : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun stationDao(): StationDao
    abstract fun stationExitDao(): StationExitDao

    companion object {
        private var instance: StationDatabase? = null

        @Synchronized
        fun getInstance(context: Context): StationDatabase? {
            if (instance == null) {
                synchronized(StationDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StationDatabase::class.java,
                        "station-database"  // 다른 데이터베이스랑 이름 겹치면 꼬임
                    ).createFromAsset("station.db")
                        .build()
                }
            }

            return instance
        }
    }
}