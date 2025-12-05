package com.anxiousflyer.peacefulflight.di

import androidx.room.Room
import com.anxiousflyer.peacefulflight.data.local.AppDatabase
import com.anxiousflyer.peacefulflight.data.repository.FlightRepository
import com.anxiousflyer.peacefulflight.data.weather.WeatherRepository
import com.anxiousflyer.peacefulflight.data.weather.WeatherService
import com.anxiousflyer.peacefulflight.utils.FlightModeManager
import com.anxiousflyer.peacefulflight.utils.TtsManager
import com.anxiousflyer.peacefulflight.viewmodel.CockpitViewModel
import com.anxiousflyer.peacefulflight.viewmodel.GuidedInterventionViewModel
import com.anxiousflyer.peacefulflight.viewmodel.LearnViewModel
import com.anxiousflyer.peacefulflight.viewmodel.MainViewModel
import com.anxiousflyer.peacefulflight.viewmodel.RealityCheckViewModel
import com.anxiousflyer.peacefulflight.viewmodel.RidingTheWaveViewModel
import com.anxiousflyer.peacefulflight.viewmodel.SosViewModel
import com.anxiousflyer.peacefulflight.viewmodel.ToolsViewModel
import com.anxiousflyer.peacefulflight.viewmodel.VoicePreviewViewModel
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
    single { com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager(androidContext()) }
    single { TtsManager(androidContext(), get()) }
    single { FlightModeManager(get()) }

    // ViewModels
    viewModel { MainViewModel(get(), get()) }
    viewModel { CockpitViewModel(get(), get(), get()) }
    viewModel { LearnViewModel() }
    viewModel { SosViewModel() }
    viewModel { ToolsViewModel() }
    viewModel { RidingTheWaveViewModel(get()) }
    viewModel { GuidedInterventionViewModel(get()) }
    viewModel { RealityCheckViewModel(get()) }
    viewModel { VoicePreviewViewModel(get(), get()) }
}
