package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SosUiState(
    val isActive: Boolean = false,
    val breathingTextRes: Int = R.string.breathe_in
)

class SosViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SosUiState())
    val uiState: StateFlow<SosUiState> = _uiState.asStateFlow()

    fun toggleSos(active: Boolean) {
        _uiState.update { it.copy(isActive = active) }
    }

    // Dummy logic for breathing animation cycle
    suspend fun startBreathingCycle() {
        while (_uiState.value.isActive) {
            _uiState.update { it.copy(breathingTextRes = R.string.breathe_in) }
            delay(4000)
            if (!_uiState.value.isActive) break
            _uiState.update { it.copy(breathingTextRes = R.string.hold) }
            delay(2000)
            if (!_uiState.value.isActive) break
            _uiState.update { it.copy(breathingTextRes = R.string.breathe_out) }
            delay(4000)
        }
    }
}
