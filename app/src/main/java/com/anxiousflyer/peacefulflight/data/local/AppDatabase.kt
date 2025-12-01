package com.anxiousflyer.peacefulflight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlightSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao
}


