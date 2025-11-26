package com.example.calmflight.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double
)

data class CockpitUiState(
    val status: FlightStatus = FlightStatus.BOARDING,
    val weather: WeatherUiState? = null
)

data class WeatherUiState(
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null,
    val temperature: Double = 0.0,
    val windSpeed: Double = 0.0,
    val weatherCode: Int = 0,
    val isGoodForTakeoff: Boolean = true,
    @StringRes val messageRes: Int = 0,
    val messageArgs: List<Any> = emptyList(),
    val cityName: String? = null
)
