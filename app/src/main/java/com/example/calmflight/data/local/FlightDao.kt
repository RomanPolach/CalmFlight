package com.example.calmflight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert
    suspend fun insertFlight(flight: FlightSession): Long

    @Update
    suspend fun updateFlight(flight: FlightSession)

    @Query("SELECT * FROM flight_sessions ORDER BY startTime DESC")
    fun getAllFlights(): Flow<List<FlightSession>>

    @Query("SELECT * FROM flight_sessions WHERE id = :id")
    suspend fun getFlightById(id: Long): FlightSession?
}


