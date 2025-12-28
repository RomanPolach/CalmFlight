package com.anxiousflyer.peacefulflight.data.weather

import android.location.Geocoder
import android.util.Log
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.model.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val api: WeatherService,
    private val geocoder: Geocoder
) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherUiState {
        return try {
            Log.d("WeatherRepo", "Fetching weather for: $lat, $lon")
            // Fetch weather API
            val response = api.getWeather(lat, lon)
            val current = response.current
            Log.d("WeatherRepo", "Current weather: $current")
            
            // Fetch City Name (Reverse Geocoding)
            val cityName = withContext(Dispatchers.IO) {
                try {
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val result = addresses?.firstOrNull()?.locality 
                        ?: addresses?.firstOrNull()?.subAdminArea
                        ?: addresses?.firstOrNull()?.adminArea
                    Log.d("WeatherRepo", "Reverse geocode result: $result")
                    result
                } catch (e: Exception) {
                    Log.e("WeatherRepo", "Geocoding failed", e)
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
                    // Fix: weather_msg_breezy expects 2 arguments: km/h and knots
                    val knots = current.windSpeed * 0.539957
                    R.string.weather_msg_breezy to listOf(current.windSpeed, knots)
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
        } catch (e: java.net.UnknownHostException) {
            Log.e("WeatherRepo", "Offline/Unknown host: ${e.message}")
            WeatherUiState(isLoading = false, errorRes = R.string.weather_error_offline)
        } catch (e: java.io.IOException) {
            Log.e("WeatherRepo", "Network IO error: ${e.message}")
            WeatherUiState(isLoading = false, errorRes = R.string.weather_error_offline)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Generic weather fetch error: ${e.javaClass.simpleName} - ${e.message}", e)
            WeatherUiState(isLoading = false, errorRes = R.string.weather_error_generic)
        }
    }
}
