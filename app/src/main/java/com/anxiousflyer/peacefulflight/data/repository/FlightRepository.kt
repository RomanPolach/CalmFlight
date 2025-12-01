package com.anxiousflyer.peacefulflight.data.repository

import com.anxiousflyer.peacefulflight.data.local.FlightDao
import com.anxiousflyer.peacefulflight.data.local.FlightSession
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


