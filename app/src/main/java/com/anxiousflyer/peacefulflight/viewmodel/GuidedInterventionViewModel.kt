package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GuidedInterventionUiState(
    val steps: List<Int> = emptyList(),
    val currentStepIndex: Int = 0,
    val currentTextRes: Int = 0,
    val isLastStep: Boolean = false,
    val anxietyScore: Float = 5f,
    val feedbackMessageRes: Int? = null,
    val showSuccessDialog: Boolean = false,
    val isAutoPlayEnabled: Boolean = false
)

class GuidedInterventionViewModel(
    private val ttsManager: TtsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuidedInterventionUiState())
    val uiState: StateFlow<GuidedInterventionUiState> = _uiState.asStateFlow()

    private val _anxietyHistory = mutableListOf<Float>()
    private var isInitialized = false

    fun initialize(stepResIds: List<Int>) {
        if (!isInitialized && stepResIds.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    steps = stepResIds,
                    currentStepIndex = 0,
                    currentTextRes = stepResIds[0],
                    isLastStep = stepResIds.size == 1,
                    isAutoPlayEnabled = false
                )
            }
            isInitialized = true
        }
    }

    fun toggleTts(text: String) {
        if (_uiState.value.isAutoPlayEnabled) {
            ttsManager.stop()
            _uiState.update { it.copy(isAutoPlayEnabled = false) }
        } else {
            ttsManager.speak(text)
            _uiState.update { it.copy(isAutoPlayEnabled = true) }
        }
    }

    fun nextStep(nextText: String) {
        ttsManager.stop() // Stop previous audio
        val state = _uiState.value
        if (state.currentStepIndex < state.steps.size - 1) {
            val newIndex = state.currentStepIndex + 1
            _uiState.update {
                it.copy(
                    currentStepIndex = newIndex,
                    currentTextRes = it.steps[newIndex],
                    isLastStep = newIndex == it.steps.size - 1
                )
            }

            if (state.isAutoPlayEnabled) {
                ttsManager.speak(nextText)
            }
        }
    }
    
    fun updateAnxietyScore(score: Float) {
        _uiState.update { it.copy(anxietyScore = score) }
    }

    fun submitRating() {
        val currentScore = _uiState.value.anxietyScore
        _anxietyHistory.add(currentScore)

        if (_anxietyHistory.size > 1) {
            val previousScore = _anxietyHistory[_anxietyHistory.size - 2]
            val feedback = when {
                currentScore < previousScore -> com.anxiousflyer.peacefulflight.R.string.feedback_improving
                currentScore > previousScore -> com.anxiousflyer.peacefulflight.R.string.feedback_worsening
                else -> com.anxiousflyer.peacefulflight.R.string.feedback_steady
            }
            _uiState.update { it.copy(feedbackMessageRes = feedback) }
        } else {
            _uiState.update { it.copy(feedbackMessageRes = null) }
        }
    }

    fun finishSession(onFinish: () -> Unit) {
        ttsManager.stop()
        val finalScore = _uiState.value.anxietyScore
        val initialScore = _anxietyHistory.firstOrNull() ?: 5f

        // Success logic: Score dropped by at least 2 OR final score is low (<= 4)
        if ((initialScore - finalScore >= 2) || finalScore <= 4) {
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


