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

}
