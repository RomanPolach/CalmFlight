package com.anxiousflyer.peacefulflight.viewmodel

import androidx.lifecycle.ViewModel
import com.anxiousflyer.peacefulflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GuidedInterventionViewModel(
    private val ttsManager: TtsManager,
    // In a real app, you'd inject a repository to get steps by tool ID
    // For now, we'll initialize with data directly or via a setter
) : ViewModel() {

    private val _steps = MutableStateFlow<List<Int>>(emptyList())
    val steps: StateFlow<List<Int>> = _steps.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _currentTextRes = MutableStateFlow(0)
    val currentTextRes: StateFlow<Int> = _currentTextRes.asStateFlow()

    private val _isLastStep = MutableStateFlow(false)
    val isLastStep: StateFlow<Boolean> = _isLastStep.asStateFlow()

    private val _anxietyScore = MutableStateFlow(5f)
    val anxietyScore: StateFlow<Float> = _anxietyScore.asStateFlow()

    private val _feedbackMessageRes = MutableStateFlow<Int?>(null)
    val feedbackMessageRes: StateFlow<Int?> = _feedbackMessageRes.asStateFlow()

    private val _anxietyHistory = mutableListOf<Float>()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()
    
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    private var shouldAutoPlay = false

    fun initialize(stepResIds: List<Int>) {
        if (stepResIds.isNotEmpty()) {
            _steps.value = stepResIds
            _currentStepIndex.value = 0
            _currentTextRes.value = stepResIds[0]
            _isLastStep.value = stepResIds.size == 1
            shouldAutoPlay = false
        }
    }

    fun toggleTts(text: String) {
        if (isSpeaking.value) {
            ttsManager.stop()
            shouldAutoPlay = false
        } else {
            ttsManager.speak(text)
            shouldAutoPlay = true
        }
    }

    fun nextStep(nextText: String) {
        ttsManager.stop() // Stop previous audio
        if (_currentStepIndex.value < _steps.value.size - 1) {
            _currentStepIndex.value += 1
            _currentTextRes.value = _steps.value[_currentStepIndex.value]
            _isLastStep.value = _currentStepIndex.value == _steps.value.size - 1
            
            if (shouldAutoPlay) {
                ttsManager.speak(nextText)
            }
        }
    }
    
    fun updateAnxietyScore(score: Float) {
        _anxietyScore.value = score
    }

    fun submitRating() {
        val currentScore = _anxietyScore.value
        _anxietyHistory.add(currentScore)

        if (_anxietyHistory.size > 1) {
            val previousScore = _anxietyHistory[_anxietyHistory.size - 2]
            if (currentScore < previousScore) {
                _feedbackMessageRes.value =
                    com.anxiousflyer.peacefulflight.R.string.feedback_improving
            } else if (currentScore > previousScore) {
                _feedbackMessageRes.value =
                    com.anxiousflyer.peacefulflight.R.string.feedback_worsening
            } else {
                _feedbackMessageRes.value = com.anxiousflyer.peacefulflight.R.string.feedback_steady
            }
        } else {
             _feedbackMessageRes.value = null
        }
    }

    fun finishSession(onFinish: () -> Unit) {
        ttsManager.stop()
        val finalScore = _anxietyScore.value
        val initialScore = _anxietyHistory.firstOrNull() ?: 5f

        // Success logic: Score dropped by at least 2 OR final score is low (<= 4)
        if ((initialScore - finalScore >= 2) || finalScore <= 4) {
            _showSuccessDialog.value = true
        } else {
            onFinish()
        }
    }

    fun closeDialog(onFinish: () -> Unit) {
        _showSuccessDialog.value = false
        onFinish()
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}


