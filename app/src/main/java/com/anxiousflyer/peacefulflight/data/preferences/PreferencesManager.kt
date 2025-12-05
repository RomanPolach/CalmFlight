package com.anxiousflyer.peacefulflight.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages user preferences for the app
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    enum class UnitSystem {
        METRIC,      // Celsius, km/h
        IMPERIAL     // Fahrenheit, mph
    }

    enum class ThemeMode {
        LIGHT,
        DARK,
        SYSTEM
    }

    /**
     * Get the current unit system preference
     */
    fun getUnitSystem(): UnitSystem {
        val value = prefs.getString(KEY_UNIT_SYSTEM, UnitSystem.IMPERIAL.name)
        return try {
            UnitSystem.valueOf(value ?: UnitSystem.IMPERIAL.name)
        } catch (e: IllegalArgumentException) {
            UnitSystem.IMPERIAL
        }
    }

    /**
     * Set the unit system preference
     */
    fun setUnitSystem(unitSystem: UnitSystem) {
        prefs.edit().putString(KEY_UNIT_SYSTEM, unitSystem.name).apply()
    }

    /**
     * Get the current theme mode preference
     */
    fun getThemeMode(): ThemeMode {
        val value = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(value ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    /**
     * Set the theme mode preference
     */
    fun setThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
    }

    /**
     * Check if using metric system
     */
    fun isMetric(): Boolean = getUnitSystem() == UnitSystem.METRIC

    /**
     * Check if using imperial system
     */
    fun isImperial(): Boolean = getUnitSystem() == UnitSystem.IMPERIAL

    /**
     * Get the saved TTS voice name
     */
    fun getTtsVoiceName(): String? {
        return prefs.getString(KEY_TTS_VOICE, null)
    }

    /**
     * Save the TTS voice name
     */
    fun setTtsVoiceName(voiceName: String) {
        prefs.edit().putString(KEY_TTS_VOICE, voiceName).apply()
    }

    /**
     * Get the saved TTS speech rate (default: 0.8)
     */
    fun getTtsSpeechRate(): Float {
        return prefs.getFloat(KEY_TTS_SPEECH_RATE, DEFAULT_SPEECH_RATE)
    }

    /**
     * Save the TTS speech rate
     */
    fun setTtsSpeechRate(rate: Float) {
        prefs.edit().putFloat(KEY_TTS_SPEECH_RATE, rate).apply()
    }

    private val _themeModeFlow = kotlinx.coroutines.flow.MutableStateFlow(getThemeMode())
    val themeModeFlow: kotlinx.coroutines.flow.StateFlow<ThemeMode> = _themeModeFlow.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_THEME_MODE) {
            _themeModeFlow.value = getThemeMode()
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private const val PREFS_NAME = "calm_flight_prefs"
        private const val KEY_UNIT_SYSTEM = "unit_system"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_TTS_VOICE = "tts_voice"
        private const val KEY_TTS_SPEECH_RATE = "tts_speech_rate"
        const val DEFAULT_SPEECH_RATE = 0.8f
    }
}
