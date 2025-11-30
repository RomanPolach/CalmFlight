package com.example.calmflight.utils

import kotlin.math.roundToInt

/**
 * Utility functions for unit conversions
 */
object UnitConverter {

    /**
     * Convert Celsius to Fahrenheit
     */
    fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9.0 / 5.0) + 32.0
    }

    /**
     * Convert km/h to mph
     */
    fun kmhToMph(kmh: Double): Double {
        return kmh * 0.621371
    }

    /**
     * Format temperature with unit
     */
    fun formatTemperature(celsius: Double, useMetric: Boolean): String {
        return if (useMetric) {
            "${celsius.roundToInt()}°C"
        } else {
            "${celsiusToFahrenheit(celsius).roundToInt()}°F"
        }
    }

    /**
     * Format wind speed with unit
     */
    fun formatWindSpeed(kmh: Double, useMetric: Boolean): String {
        return if (useMetric) {
            "${kmh.roundToInt()} km/h"
        } else {
            "${kmhToMph(kmh).roundToInt()} mph"
        }
    }
}
