package com.anxiousflyer.peacefulflight.viewmodel

import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
import com.anxiousflyer.peacefulflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VoiceInfo(
    val voice: Voice,
    val displayName: String,
    val locale: String,
    val quality: String,
    val gender: String
)

data class VoicePreviewUiState(
    val voices: List<VoiceInfo> = emptyList(),
    val currentVoice: Voice? = null,
    val isSpeaking: Boolean = false,
    val selectedVoice: Voice? = null,
    val speechRate: Float = PreferencesManager.DEFAULT_SPEECH_RATE,
    val isLoading: Boolean = true
)

class VoicePreviewViewModel(
    private val ttsManager: TtsManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoicePreviewUiState())
    val uiState: StateFlow<VoicePreviewUiState> = _uiState.asStateFlow()

    init {
        val rate = preferencesManager.getTtsSpeechRate()
        _uiState.update { it.copy(speechRate = rate) }
        
        viewModelScope.launch {
            ttsManager.isInitialized.collect { initialized ->
                if (initialized) {
                    loadVoices()
                    val voice = ttsManager.getTts()?.voice
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentVoice = voice,
                            selectedVoice = voice
                        )
                    }
                }
            }
        }
    }

    private fun loadVoices() {
        val allVoices = ttsManager.getVoices()
        
        // Filter for local UK English voices only
        val localEnglishVoices = allVoices.filter {
            it.locale.language.equals("en", ignoreCase = true) &&
                    it.locale.country.equals("GB", ignoreCase = true) &&
                    !it.isNetworkConnectionRequired
        }.sortedByDescending { it.quality }

        val voiceInfos = localEnglishVoices.map { voice ->
            VoiceInfo(
                voice = voice,
                displayName = formatVoiceName(voice.name),
                locale = voice.locale.displayName,
                quality = getQualityString(voice.quality),
                gender = detectGender(voice.name)
            )
        }
        _uiState.update { it.copy(voices = voiceInfos) }
    }

    private fun formatVoiceName(name: String): String {
        // Extract a more readable name from the voice identifier
        return name
            .replace("en-", "")
            .replace("_", " ")
            .replace("-", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            .take(30)
    }

    private fun getQualityString(quality: Int): String {
        return when (quality) {
            Voice.QUALITY_VERY_LOW -> "Very Low"
            Voice.QUALITY_LOW -> "Low"
            Voice.QUALITY_NORMAL -> "Normal"
            Voice.QUALITY_HIGH -> "High"
            Voice.QUALITY_VERY_HIGH -> "Very High"
            else -> "Unknown"
        }
    }

    private fun detectGender(voiceName: String): String {
        val lowerName = voiceName.lowercase()

        return when {
            lowerName.contains("female") -> "Female"
            lowerName.contains("male") -> "Male"
            lowerName.contains("woman") -> "Female"
            lowerName.contains("man") -> "Male"
            lowerName.contains("#f") || lowerName.contains("-f") || lowerName.contains("_f") -> "Female"
            lowerName.contains("#m") || lowerName.contains("-m") || lowerName.contains("_m") -> "Male"
            else -> "Unknown"
        }
    }

    fun previewVoice(voiceInfo: VoiceInfo) {
        ttsManager.stop()
        ttsManager.setVoice(voiceInfo.voice)
        _uiState.update { it.copy(selectedVoice = voiceInfo.voice) }
        
        val sampleText = "You are safe. The plane is flying normally. Take a deep breath and relax."
        ttsManager.speak(sampleText)
    }

    fun selectVoice(voiceInfo: VoiceInfo) {
        ttsManager.setVoice(voiceInfo.voice)
        _uiState.update {
            it.copy(
                selectedVoice = voiceInfo.voice,
                currentVoice = voiceInfo.voice
            )
        }
        // Save to preferences
        preferencesManager.setTtsVoiceName(voiceInfo.voice.name)
    }

    fun stopPreview() {
        ttsManager.stop()
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
        ttsManager.setSpeechRate(rate)
        preferencesManager.setTtsSpeechRate(rate)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
