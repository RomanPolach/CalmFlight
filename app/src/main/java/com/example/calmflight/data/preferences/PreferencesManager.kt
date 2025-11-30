package com.example.calmflight.data.preferences

import android.content.Context
import android.content.SharedPreferences

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
     * Check if using metric system
     */
    fun isMetric(): Boolean = getUnitSystem() == UnitSystem.METRIC

    /**
     * Check if using imperial system
     */
    fun isImperial(): Boolean = getUnitSystem() == UnitSystem.IMPERIAL

    companion object {
        private const val PREFS_NAME = "calm_flight_prefs"
        private const val KEY_UNIT_SYSTEM = "unit_system"
    }
}
