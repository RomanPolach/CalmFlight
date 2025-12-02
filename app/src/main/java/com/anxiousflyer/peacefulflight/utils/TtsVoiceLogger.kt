package com.anxiousflyer.peacefulflight.utils

import android.util.Log
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

class TtsVoiceLogger {
    
    companion object {
        private const val TAG = "TTS_Voices"
    }
    
    /**
     * Logs all available TTS voices with detailed information
     */
    fun logAllVoices(tts: TextToSpeech?) {
        if (tts == null) {
            Log.e(TAG, "TTS is null - cannot log voices")
            return
        }
        
        val voices = tts.voices?.toList() ?: emptyList()
        
        Log.d(TAG, "=== Available TTS Voices (${voices.size} total) ===")
        
        voices.forEachIndexed { index, voice ->
            Log.d(TAG, """
                Voice #$index:
                Name: ${voice.name}
                Locale: ${voice.locale.displayName} (${voice.locale.language}-${voice.locale.country})
                Quality: ${getQualityDescription(voice.quality)}
                Latency: ${getLatencyDescription(voice.latency)}
                Requires Network: ${voice.isNetworkConnectionRequired}
                Features: ${voice.features.joinToString(", ") { it.toString() }}
            """.trimIndent())
        }
        
        if (voices.isEmpty()) {
            Log.w(TAG, "No voices available!")
        }
    }
    
    /**
     * Logs voices grouped by language for easier selection
     */
    fun logVoicesByLanguage(tts: TextToSpeech?) {
        if (tts == null) {
            Log.e(TAG, "TTS is null - cannot log voices")
            return
        }
        
        val voices = tts.voices?.toList() ?: emptyList()
        val groupedVoices = voices.groupBy { it.locale.language }
        
        Log.d(TAG, "=== Available TTS Voices by Language ===")
        
        groupedVoices.forEach { (language, languageVoices) ->
            Log.d(TAG, "\n--- $language (${languageVoices.size} voices) ---")
            languageVoices.forEachIndexed { index, voice ->
                Log.d(TAG, "  ${index + 1}. ${voice.name} (${voice.locale.displayName}) - ${getQualityDescription(voice.quality)}")
            }
        }
    }
    
    /**
     * Logs only high-quality offline voices
     */
    fun logBestOfflineVoices(tts: TextToSpeech?) {
        if (tts == null) {
            Log.e(TAG, "TTS is null - cannot log voices")
            return
        }
        
        val voices = tts.voices?.toList() ?: emptyList()
        val bestVoices = voices.filter { 
            !it.isNetworkConnectionRequired && 
            it.quality >= Voice.QUALITY_NORMAL 
        }.sortedByDescending { it.quality }
        
        Log.d(TAG, "=== Best Offline Voices (${bestVoices.size} total) ===")
        
        bestVoices.forEachIndexed { index, voice ->
            Log.d(TAG, "${index + 1}. ${voice.name} (${voice.locale.displayName}) - ${getQualityDescription(voice.quality)}")
        }
        
        if (bestVoices.isEmpty()) {
            Log.w(TAG, "No high-quality offline voices found!")
        }
    }
    
    /**
     * Finds and logs voices for a specific language
     */
    fun logVoicesForLanguage(tts: TextToSpeech?, language: String) {
        if (tts == null) {
            Log.e(TAG, "TTS is null - cannot log voices")
            return
        }
        
        val voices = tts.voices?.toList() ?: emptyList()
        val languageVoices = voices.filter { it.locale.language.equals(language, ignoreCase = true) }
        
        Log.d(TAG, "=== Voices for language: $language (${languageVoices.size} found) ===")
        
        languageVoices.forEachIndexed { index, voice ->
            Log.d(TAG, """
                ${index + 1}. ${voice.name}
                Locale: ${voice.locale.displayName}
                Quality: ${getQualityDescription(voice.quality)}
                Network Required: ${voice.isNetworkConnectionRequired}
            """.trimIndent())
        }
        
        if (languageVoices.isEmpty()) {
            Log.w(TAG, "No voices found for language: $language")
        }
    }
    
    private fun getQualityDescription(quality: Int): String {
        return when (quality) {
            Voice.QUALITY_VERY_LOW -> "Very Low"
            Voice.QUALITY_LOW -> "Low"
            Voice.QUALITY_NORMAL -> "Normal"
            Voice.QUALITY_HIGH -> "High"
            Voice.QUALITY_VERY_HIGH -> "Very High"
            else -> "Unknown ($quality)"
        }
    }
    
    private fun getLatencyDescription(latency: Int): String {
        return when (latency) {
            Voice.LATENCY_VERY_LOW -> "Very Low"
            Voice.LATENCY_LOW -> "Low"
            Voice.LATENCY_NORMAL -> "Normal"
            Voice.LATENCY_HIGH -> "High"
            else -> "Unknown ($latency)"
        }
    }
}
