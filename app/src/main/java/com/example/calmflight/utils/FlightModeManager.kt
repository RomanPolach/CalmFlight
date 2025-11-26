package com.example.calmflight.utils

import com.example.calmflight.data.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlightModeManager(private val repository: FlightRepository) {
    
    private val _isFlightActive = MutableStateFlow(false)
    val isFlightActive: StateFlow<Boolean> = _isFlightActive.asStateFlow()
    
    private var currentFlightId: Long? = null

    suspend fun startFlight(expectedFear: Int) {
        val id = repository.startFlight(expectedFear)
        currentFlightId = id
        _isFlightActive.value = true
    }

    suspend fun endFlight(actualFear: Int) {
        currentFlightId?.let { id ->
            repository.endFlight(id, actualFear)
        }
        currentFlightId = null
        _isFlightActive.value = false
    }
}


