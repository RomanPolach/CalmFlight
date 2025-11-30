package com.example.calmflight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmflight.data.AppContent
import com.example.calmflight.data.preferences.PreferencesManager
import com.example.calmflight.data.weather.WeatherRepository
import com.example.calmflight.model.CockpitUiState
import com.example.calmflight.model.FlightStatus
import com.example.calmflight.model.Tool
import com.example.calmflight.model.WeatherUiState
import com.example.calmflight.utils.FlightModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CockpitViewModel(
    private val flightModeManager: FlightModeManager,
    private val weatherRepository: WeatherRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CockpitUiState())
    val uiState: StateFlow<CockpitUiState> = _uiState.asStateFlow()
    
    val isFlightActive: StateFlow<Boolean> = flightModeManager.isFlightActive

    init {
        refreshSettings()
    }

    fun setStatus(newStatus: FlightStatus) {
        _uiState.update { it.copy(status = newStatus) }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(weather = WeatherUiState(isLoading = true)) }
            val result = weatherRepository.getWeather(lat, lon)
            _uiState.update { it.copy(weather = result) }
        }
    }

    fun toggleSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
        if (!show) {
            refreshSettings()
        }
    }

    fun refreshSettings() {
        _uiState.update { it.copy(isMetric = preferencesManager.isMetric()) }
    }

    fun getRecommendedTools(): List<Tool> {
        // Just return top 2 for dashboard
        return AppContent.tools.take(2)
    }
}
