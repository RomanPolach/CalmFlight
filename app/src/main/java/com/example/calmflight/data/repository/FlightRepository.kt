package com.example.calmflight.data.repository

import com.example.calmflight.data.local.FlightDao
import com.example.calmflight.data.local.FlightSession
import kotlinx.coroutines.flow.Flow

class FlightRepository(private val flightDao: FlightDao) {
    
    val allFlights: Flow<List<FlightSession>> = flightDao.getAllFlights()
    
    suspend fun startFlight(expectedFear: Int): Long {
        val flight = FlightSession(
            startTime = System.currentTimeMillis(),
            expectedFear = expectedFear
        )
        return flightDao.insertFlight(flight)
    }
    
    suspend fun endFlight(flightId: Long, actualFear: Int) {
        val flight = flightDao.getFlightById(flightId)
        if (flight != null) {
            val updatedFlight = flight.copy(
                endTime = System.currentTimeMillis(),
                actualFear = actualFear
            )
            flightDao.updateFlight(updatedFlight)
        }
    }
    
    suspend fun getFlight(id: Long): FlightSession? {
        return flightDao.getFlightById(id)
    }
}


