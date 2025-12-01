package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anxiousflyer.peacefulflight.utils.FlightModeManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val flightModeManager: FlightModeManager,
    private val preferencesManager: com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
) : ViewModel() {

    val themeMode = preferencesManager.themeModeFlow
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


