package com.anxiousflyer.peacefulflight.viewmodel

import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
import com.anxiousflyer.peacefulflight.utils.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VoiceInfo(
    val voice: Voice,
    val displayName: String,
    val locale: String,
    val quality: String,
    val gender: String
)

class VoicePreviewViewModel(
    private val ttsManager: TtsManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _voices = MutableStateFlow<List<VoiceInfo>>(emptyList())
    val voices: StateFlow<List<VoiceInfo>> = _voices.asStateFlow()

    private val _currentVoice = MutableStateFlow<Voice?>(null)
    val currentVoice: StateFlow<Voice?> = _currentVoice.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _selectedVoice = MutableStateFlow<Voice?>(null)
    val selectedVoice: StateFlow<Voice?> = _selectedVoice.asStateFlow()

    private val _speechRate = MutableStateFlow(PreferencesManager.DEFAULT_SPEECH_RATE)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    val isLoading: StateFlow<Boolean> = ttsManager.isInitialized

    init {
        _speechRate.value = preferencesManager.getTtsSpeechRate()
        
        viewModelScope.launch {
            ttsManager.isInitialized.collect { initialized ->
                if (initialized) {
                    loadVoices()
                    _currentVoice.value = ttsManager.getTts()?.voice
                    _selectedVoice.value = _currentVoice.value
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

        _voices.value = localEnglishVoices.map { voice ->
            VoiceInfo(
                voice = voice,
                displayName = formatVoiceName(voice.name),
                locale = voice.locale.displayName,
                quality = getQualityString(voice.quality),
                gender = detectGender(voice.name)
            )
        }
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
        _selectedVoice.value = voiceInfo.voice
        
        val sampleText = "You are safe. The plane is flying normally. Take a deep breath and relax."
        ttsManager.speak(sampleText)
    }

    fun selectVoice(voiceInfo: VoiceInfo) {
        ttsManager.setVoice(voiceInfo.voice)
        _selectedVoice.value = voiceInfo.voice
        _currentVoice.value = voiceInfo.voice
        // Save to preferences
        preferencesManager.setTtsVoiceName(voiceInfo.voice.name)
    }

    fun stopPreview() {
        ttsManager.stop()
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
        ttsManager.setSpeechRate(rate)
        preferencesManager.setTtsSpeechRate(rate)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
