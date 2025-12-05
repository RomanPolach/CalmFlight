package com.anxiousflyer.peacefulflight.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context, private val preferencesManager: PreferencesManager) {

    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle error - for now we assume US English is available or fallback
                } else {
                    _isInitialized.value = true

                    // Restore saved preferences
                    restoreSavedVoice()
                    restoreSavedSpeechRate()

                    // Set utterance listener after initialization
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })

                    // Log available voices for debugging
                    logAvailableVoices()
                }
            }
        }
    }

    private fun restoreSavedVoice() {
        val savedVoiceName = preferencesManager.getTtsVoiceName() ?: return
        val voices = tts?.voices ?: return
        val savedVoice = voices.find { it.name == savedVoiceName }
        if (savedVoice != null) {
            tts?.voice = savedVoice
            Log.d("TtsManager", "Restored saved voice: $savedVoiceName")
        }
    }

    private fun restoreSavedSpeechRate() {
        val savedRate = preferencesManager.getTtsSpeechRate()
        tts?.setSpeechRate(savedRate)
        Log.d("TtsManager", "Restored saved speech rate: $savedRate")
    }

    fun speak(text: String) {
        if (_isInitialized.value) {
            // Stop whatever is currently playing
            stop()

            // Split text by paragraphs and add pauses between them
            val paragraphs = text.split("\n\n").filter { it.isNotBlank() }
            val params = android.os.Bundle()

            paragraphs.forEachIndexed { index, paragraph ->
                // Speak the paragraph
                val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                tts?.speak(paragraph.trim(), queueMode, params, "TTS_ID_$index")

                // Add 1 second pause after each paragraph (except the last one)
                if (index < paragraphs.size - 1) {
                    tts?.playSilentUtterance(1000L, TextToSpeech.QUEUE_ADD, "PAUSE_$index")
                }
            }
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }

    fun getVoices(): List<android.speech.tts.Voice> {
        return tts?.voices?.toList() ?: emptyList()
    }

    fun setVoice(voice: android.speech.tts.Voice) {
        tts?.voice = voice
    }

    fun getTts(): TextToSpeech? = tts

    /**
     * Automatically logs available TTS voices when initialization completes
     */
    private fun logAvailableVoices() {
        try {
            val voices = tts?.voices?.toList() ?: emptyList()
            Log.d("TTS_Voices", "=== TTS Initialized - Local English Voices Only ===")

            // Filter for English voices that are offline only
            val localEnglishVoices = voices.filter {
                it.locale.language.equals("en", ignoreCase = true) &&
                        !it.isNetworkConnectionRequired
            }

            if (localEnglishVoices.isEmpty()) {
                Log.w("TTS_Voices", "No local English voices found!")
                return
            }

            Log.d("TTS_Voices", "Found ${localEnglishVoices.size} local English voices:")

            localEnglishVoices.forEachIndexed { index, voice ->
                val quality = when (voice.quality) {
                    android.speech.tts.Voice.QUALITY_VERY_LOW -> "Very Low"
                    android.speech.tts.Voice.QUALITY_LOW -> "Low"
                    android.speech.tts.Voice.QUALITY_NORMAL -> "Normal"
                    android.speech.tts.Voice.QUALITY_HIGH -> "High"
                    android.speech.tts.Voice.QUALITY_VERY_HIGH -> "Very High"
                    else -> "Unknown"
                }

                val latency = when (voice.latency) {
                    android.speech.tts.Voice.LATENCY_VERY_LOW -> "Very Fast"
                    android.speech.tts.Voice.LATENCY_LOW -> "Fast"
                    android.speech.tts.Voice.LATENCY_NORMAL -> "Normal"
                    android.speech.tts.Voice.LATENCY_HIGH -> "Slow"
                    else -> "Unknown"
                }

                // Detect gender from voice name
                val gender = detectGender(voice.name)

                val features = voice.features.joinToString(", ") {
                    when (it) {
                        "keyFeatureNetworkRetrieval" -> "Network Retrieval"
                        "keyFeatureEmbeddedSynthesis" -> "Embedded Synthesis"
                        "keyFeatureInternetConnection" -> "Internet Connection"
                        else -> it
                    }
                }

                Log.d(
                    "TTS_Voices", """
                    
                    Voice #${index + 1}:
                    Name: ${voice.name}
                    Gender: $gender
                    Locale: ${voice.locale.displayName} (${voice.locale.language}-${voice.locale.country})
                    Quality: $quality
                    Latency: $latency
                    Features: ${if (features.isEmpty()) "None" else features}
                    Voice ID: ${voice.name}
                """.trimIndent()
                )
            }

            // Show best local English voices by quality
            val bestLocalEnglish = localEnglishVoices.filter {
                it.quality >= android.speech.tts.Voice.QUALITY_NORMAL
            }.sortedByDescending { it.quality }

            if (bestLocalEnglish.isNotEmpty()) {
                Log.d("TTS_Voices", "\n=== Best Local English Voices ===")
                bestLocalEnglish.forEachIndexed { index, voice ->
                    val quality = when (voice.quality) {
                        android.speech.tts.Voice.QUALITY_NORMAL -> "Normal"
                        android.speech.tts.Voice.QUALITY_HIGH -> "High"
                        android.speech.tts.Voice.QUALITY_VERY_HIGH -> "Very High"
                        else -> "Unknown"
                    }
                    val gender = detectGender(voice.name)
                    Log.d(
                        "TTS_Voices",
                        "${index + 1}. ${voice.name} (${voice.locale.displayName}) - $gender - $quality"
                    )
                }
            }

            // Group by gender for easy selection
            val maleVoices = localEnglishVoices.filter { detectGender(it.name) == "Male" }
            val femaleVoices = localEnglishVoices.filter { detectGender(it.name) == "Female" }
            val unknownVoices = localEnglishVoices.filter { detectGender(it.name) == "Unknown" }

            Log.d("TTS_Voices", "\n=== Voices by Gender ===")
            if (maleVoices.isNotEmpty()) {
                Log.d("TTS_Voices", "Male voices (${maleVoices.size}):")
                maleVoices.forEach { voice ->
                    Log.d("TTS_Voices", "  - ${voice.name} (${voice.locale.displayName})")
                }
            }
            if (femaleVoices.isNotEmpty()) {
                Log.d("TTS_Voices", "Female voices (${femaleVoices.size}):")
                femaleVoices.forEach { voice ->
                    Log.d("TTS_Voices", "  - ${voice.name} (${voice.locale.displayName})")
                }
            }
            if (unknownVoices.isNotEmpty()) {
                Log.d("TTS_Voices", "Unknown gender (${unknownVoices.size}):")
                unknownVoices.forEach { voice ->
                    Log.d("TTS_Voices", "  - ${voice.name} (${voice.locale.displayName})")
                }
            }

            // Show current voice if it's local English
            val currentVoice = tts?.voice
            if (currentVoice != null &&
                currentVoice.locale.language.equals("en", ignoreCase = true) &&
                !currentVoice.isNetworkConnectionRequired
            ) {
                val gender = detectGender(currentVoice.name)
                Log.d("TTS_Voices", "\n=== Currently Active Voice ===")
                Log.d(
                    "TTS_Voices",
                    "Active: ${currentVoice.name} (${currentVoice.locale.displayName}) - $gender"
                )
            }

        } catch (e: Exception) {
            Log.e("TTS_Voices", "Error logging voices: ${e.message}")
        }
    }

    /**
     * Detects gender from voice name based on common patterns
     */
    private fun detectGender(voiceName: String): String {
        val lowerName = voiceName.lowercase()

        // Check for explicit gender indicators
        when {
            lowerName.contains("female") -> return "Female"
            lowerName.contains("male") -> return "Male"
            lowerName.contains("woman") -> return "Female"
            lowerName.contains("man") -> return "Male"
            lowerName.contains("girl") -> return "Female"
            lowerName.contains("boy") -> return "Male"
            lowerName.contains("lady") -> return "Female"
            lowerName.contains("gentleman") -> return "Male"
        }

        // Check for common gender-specific name patterns
        when {
            lowerName.contains("#f") || lowerName.contains("-f") || lowerName.contains("_f") -> return "Female"
            lowerName.contains("#m") || lowerName.contains("-m") || lowerName.contains("_m") -> return "Male"
        }

        // Check for common female voice indicators
        when {
            lowerName.contains("susan") || lowerName.contains("mary") || lowerName.contains("jane") ||
                    lowerName.contains("lisa") || lowerName.contains("sarah") || lowerName.contains(
                "emma"
            ) ||
                    lowerName.contains("anna") || lowerName.contains("karen") || lowerName.contains(
                "diana"
            ) -> return "Female"
        }

        // Check for common male voice indicators
        when {
            lowerName.contains("john") || lowerName.contains("david") || lowerName.contains("michael") ||
                    lowerName.contains("robert") || lowerName.contains("james") || lowerName.contains(
                "william"
            ) ||
                    lowerName.contains("richard") || lowerName.contains("thomas") || lowerName.contains(
                "mark"
            ) -> return "Male"
        }

        return "Unknown"
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

