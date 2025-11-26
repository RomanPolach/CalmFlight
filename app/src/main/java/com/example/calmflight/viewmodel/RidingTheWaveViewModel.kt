package com.example.calmflight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calmflight.R
import com.example.calmflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RidingTheWaveViewModel(
    private val ttsManager: TtsManager
) : ViewModel() {

    private val steps = listOf(
        R.string.rtw_intro,
        R.string.rtw_step_1,
        R.string.rtw_step_2,
        R.string.rtw_step_3,
        R.string.rtw_step_4,
        R.string.rtw_step_5,
        R.string.rtw_step_6,
        R.string.rtw_step_7,
        R.string.rtw_step_8,
        R.string.rtw_step_9,
        R.string.rtw_step_10
    )

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _currentTextRes = MutableStateFlow(steps[0])
    val currentTextRes: StateFlow<Int> = _currentTextRes.asStateFlow()

    private val _isLastStep = MutableStateFlow(false)
    val isLastStep: StateFlow<Boolean> = _isLastStep.asStateFlow()

    // Anxiety Tracking
    private val _anxietyScore = MutableStateFlow(5f)
    val anxietyScore: StateFlow<Float> = _anxietyScore.asStateFlow()

    private val _feedbackMessageRes = MutableStateFlow<Int?>(null)
    val feedbackMessageRes: StateFlow<Int?> = _feedbackMessageRes.asStateFlow()

    private val anxietyHistory = mutableListOf<Int>()

    // Completion
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    // TTS State
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    private var shouldAutoPlay = false

    fun toggleTts(text: String) {
        if (isSpeaking.value) {
            ttsManager.stop()
            shouldAutoPlay = false
        } else {
            ttsManager.speak(text)
            shouldAutoPlay = true
        }
    }

    // Called by UI when the step content changes (e.g. after nextStep)
    fun onStepContentChanged(text: String) {
        if (shouldAutoPlay) {
            ttsManager.speak(text)
        }
    }

    fun nextStep() {
        ttsManager.stop() // Stop previous audio immediately
        if (_currentStepIndex.value < steps.size - 1) {
            _currentStepIndex.value += 1
            _currentTextRes.value = steps[_currentStepIndex.value]
            _isLastStep.value = _currentStepIndex.value == steps.size - 1
        }
    }

    fun updateAnxietyScore(score: Float) {
        _anxietyScore.value = score
    }

    fun submitRating() {
        val currentScore = _anxietyScore.value.toInt()
        val previousScore = anxietyHistory.lastOrNull()

        anxietyHistory.add(currentScore)

        if (previousScore != null) {
            when {
                currentScore < previousScore -> _feedbackMessageRes.value = R.string.feedback_improving
                currentScore > previousScore -> _feedbackMessageRes.value = R.string.feedback_worsening
                else -> _feedbackMessageRes.value = R.string.feedback_steady
            }
        } else {
             _feedbackMessageRes.value = R.string.feedback_steady
        }
    }

    fun finishSession(onFinish: () -> Unit) {
        val startScore = anxietyHistory.firstOrNull() ?: 10
        val endScore = anxietyHistory.lastOrNull() ?: _anxietyScore.value.toInt()

        // Simple logic: If anxiety dropped by at least 2 points OR is low (<= 4), consider it managed.
        if ((startScore - endScore >= 2) || endScore <= 4) {
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
        ttsManager.shutdown()
    }
}
