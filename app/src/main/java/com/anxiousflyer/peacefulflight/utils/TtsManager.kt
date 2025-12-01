package com.anxiousflyer.peacefulflight.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {

    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Handle error - for now we assume US English is available or fallback
                } else {
                    isInitialized = true
                    // Set a slightly slower, calmer default rate
                    tts?.setSpeechRate(0.7f)
                }
            }
        }

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
    }

    fun speak(text: String) {
        if (isInitialized) {
            // Stop whatever is currently playing
            stop()
            // Queue the new text
            val params = android.os.Bundle()
            // Request ID for the listener
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "TTS_ID")
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

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

