package com.example.calmflight.data.weather

import android.location.Geocoder
import com.example.calmflight.R
import com.example.calmflight.model.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val api: WeatherService,
    private val geocoder: Geocoder
) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherUiState {
        return try {
            // Fetch weather API
            val response = api.getWeather(lat, lon)
            val current = response.current
            
            // Fetch City Name (Reverse Geocoding)
            val cityName = withContext(Dispatchers.IO) {
                try {
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    addresses?.firstOrNull()?.locality 
                        ?: addresses?.firstOrNull()?.subAdminArea
                        ?: addresses?.firstOrNull()?.adminArea
                } catch (e: Exception) {
                    null
                }
            }
            
            val isCalmWeather = current.weatherCode <= 3
            val isLowWind = current.windSpeed < 25.0

            // Parse weather code for detailed information
            val weatherInfo = WeatherCodeParser.getWeatherInfo(current.weatherCode)
            
            val isGoodForTakeoff = isCalmWeather && isLowWind
            
            val (msgRes, msgArgs) = if (isGoodForTakeoff) {
                R.string.weather_msg_excellent to emptyList()
            } else {
                if (!isLowWind) {
                    R.string.weather_msg_breezy to listOf(current.windSpeed)
                } else {
                    R.string.weather_msg_cloudy to emptyList()
                }
            }

            WeatherUiState(
                isLoading = false,
                temperature = current.temperature,
                windSpeed = current.windSpeed,
                weatherCode = current.weatherCode,
                weatherDescription = weatherInfo.description,
                passengerMessage = weatherInfo.passengerMessage,
                isGoodForTakeoff = isGoodForTakeoff,
                messageRes = msgRes,
                messageArgs = msgArgs,
                cityName = cityName
            )
        } catch (e: Exception) {
            val errorRes = when (e) {
                is java.net.UnknownHostException, is java.io.IOException -> R.string.weather_error_offline
                else -> R.string.weather_error_generic
            }
            WeatherUiState(isLoading = false, errorRes = errorRes)
        }
    }
}
