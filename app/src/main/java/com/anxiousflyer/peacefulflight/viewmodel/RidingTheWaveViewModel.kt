package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RidingTheWaveUiState(
    val currentStepIndex: Int = 0,
    val currentTextRes: Int = R.string.rtw2_intro,
    val isLastStep: Boolean = false,
    val anxietyScore: Float = 5f,
    val feedbackMessageRes: Int? = null,
    val showSuccessDialog: Boolean = false,
    val isAutoPlayEnabled: Boolean = false
)

class RidingTheWaveViewModel(
    private val ttsManager: TtsManager
) : ViewModel() {

    private val steps = listOf(
        R.string.rtw2_intro,
        R.string.rtw2_step_1,
        R.string.rtw2_step_2,
        R.string.rtw2_step_3,
        R.string.rtw2_step_4,
        R.string.rtw2_step_5,
        R.string.rtw2_step_6,
        R.string.rtw2_step_7,
        R.string.rtw2_step_8,
        R.string.rtw2_step_9,
        R.string.rtw2_step_10,
        R.string.rtw2_step_11
    )

    private val _uiState = MutableStateFlow(RidingTheWaveUiState())
    val uiState: StateFlow<RidingTheWaveUiState> = _uiState.asStateFlow()

    private val anxietyHistory = mutableListOf<Int>()

    fun toggleTts(text: String) {
        if (_uiState.value.isAutoPlayEnabled) {
            ttsManager.stop()
            _uiState.update { it.copy(isAutoPlayEnabled = false) }
        } else {
            ttsManager.speak(text)
            _uiState.update { it.copy(isAutoPlayEnabled = true) }
        }
    }

    // Called by UI when the step content changes (e.g. after nextStep)
    fun onStepContentChanged(text: String) {
        if (_uiState.value.isAutoPlayEnabled) {
            ttsManager.speak(text)
        }
    }

    fun nextStep() {
        ttsManager.stop() // Stop previous audio immediately
        val currentIndex = _uiState.value.currentStepIndex
        if (currentIndex < steps.size - 1) {
            val newIndex = currentIndex + 1
            _uiState.update {
                it.copy(
                    currentStepIndex = newIndex,
                    currentTextRes = steps[newIndex],
                    isLastStep = newIndex == steps.size - 1
                )
            }
        }
    }

    fun updateAnxietyScore(score: Float) {
        _uiState.update { it.copy(anxietyScore = score) }
    }

    fun submitRating() {
        val currentScore = _uiState.value.anxietyScore.toInt()
        anxietyHistory.add(currentScore)
    }

    fun finishSession(onFinish: () -> Unit) {
        val startScore = anxietyHistory.firstOrNull()
        val endScore = anxietyHistory.lastOrNull()

        // Simple logic: If anxiety dropped by at least 2 points OR is low (<= 4), consider it managed.
        if (startScore != null && endScore != null && (startScore - endScore >= 2)) {
            _uiState.update { it.copy(showSuccessDialog = true) }
        } else {
            onFinish()
        }
    }
    
    fun closeDialog(onFinish: () -> Unit) {
        _uiState.update { it.copy(showSuccessDialog = false) }
        onFinish()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
