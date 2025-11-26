package com.example.calmflight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmflight.utils.FlightModeManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val flightModeManager: FlightModeManager
) : ViewModel() {

    val isFlightActive: StateFlow<Boolean> = flightModeManager.isFlightActive

    fun startFlight(expectedFear: Int) {
        viewModelScope.launch {
            flightModeManager.startFlight(expectedFear)
        }
    }

    fun endFlight(actualFear: Int) {
        viewModelScope.launch {
            flightModeManager.endFlight(actualFear)
        }
    }
}


