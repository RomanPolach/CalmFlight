package com.anxiousflyer.peacefulflight.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

@Keep
data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather
)

@Keep
data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double
)

data class CockpitUiState(
    val status: FlightStatus = FlightStatus.BOARDING,
    val weather: WeatherUiState? = null,
    val isMetric: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val themeMode: com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager.ThemeMode = com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager.ThemeMode.SYSTEM,
    val isFlightActive: Boolean = false
)

data class WeatherUiState(
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null,
    val temperature: Double = 0.0,
    val windSpeed: Double = 0.0,
    val weatherCode: Int = 0,
    val weatherDescription: String = "",
    val passengerMessage: String = "",  // Reassuring message for nervous flyers
    val isGoodForTakeoff: Boolean = true,
    @StringRes val messageRes: Int = 0,
    val messageArgs: List<Any> = emptyList(),
    val cityName: String? = null
)
