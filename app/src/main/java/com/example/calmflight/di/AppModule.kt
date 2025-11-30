package com.example.calmflight.di

import androidx.room.Room
import com.example.calmflight.data.local.AppDatabase
import com.example.calmflight.data.repository.FlightRepository
import com.example.calmflight.data.weather.WeatherRepository
import com.example.calmflight.data.weather.WeatherService
import com.example.calmflight.utils.FlightModeManager
import com.example.calmflight.utils.TtsManager
import com.example.calmflight.viewmodel.CockpitViewModel
import com.example.calmflight.viewmodel.GuidedInterventionViewModel
import com.example.calmflight.viewmodel.LearnViewModel
import com.example.calmflight.viewmodel.MainViewModel
import com.example.calmflight.viewmodel.RealityCheckViewModel
import com.example.calmflight.viewmodel.RidingTheWaveViewModel
import com.example.calmflight.viewmodel.SosViewModel
import com.example.calmflight.viewmodel.ToolsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // Network
    single {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
    single { android.location.Geocoder(androidContext(), java.util.Locale.getDefault()) }
    single { WeatherRepository(get(), get()) }

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "calmflight_db"
        ).build()
    }
    single { get<AppDatabase>().flightDao() }
    single { FlightRepository(get()) }

    // Utils
    single { TtsManager(androidContext()) }
    single { FlightModeManager(get()) }
    single { com.example.calmflight.data.preferences.PreferencesManager(androidContext()) }

    // ViewModels
    viewModel { MainViewModel(get()) }
    viewModel { CockpitViewModel(get(), get(), get()) }
    viewModel { LearnViewModel() }
    viewModel { SosViewModel() }
    viewModel { ToolsViewModel() }
    viewModel { RidingTheWaveViewModel(get()) }
    viewModel { GuidedInterventionViewModel(get()) }
    viewModel { RealityCheckViewModel(get()) }
}
