package com.example.calmflight.data.weather

import com.example.calmflight.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast?current=temperature_2m,weather_code,wind_speed_10m")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): WeatherResponse
}

