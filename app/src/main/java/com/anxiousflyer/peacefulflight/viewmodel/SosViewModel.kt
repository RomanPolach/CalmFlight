package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SosViewModel : ViewModel() {
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _breathingTextRes = MutableStateFlow(R.string.breathe_in)
    val breathingTextRes: StateFlow<Int> = _breathingTextRes.asStateFlow()

    fun toggleSos(active: Boolean) {
        _isActive.value = active
    }

    // Dummy logic for breathing animation cycle
    suspend fun startBreathingCycle() {
        while(_isActive.value) {
            _breathingTextRes.value = R.string.breathe_in
            delay(4000)
            if (!_isActive.value) break
            _breathingTextRes.value = R.string.hold
            delay(2000)
            if (!_isActive.value) break
            _breathingTextRes.value = R.string.breathe_out
            delay(4000)
        }
    }
}
