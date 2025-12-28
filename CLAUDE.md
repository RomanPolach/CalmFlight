# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (ProGuard enabled, resource shrinking)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Install debug build on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

## Architecture Overview

PeacefulFlight is an Android app for managing flight anxiety. It uses modern Android architecture:

```
UI Layer (Jetpack Compose + Material 3)
    ↓
ViewModel Layer (9 ViewModels, Koin-injected)
    ↓
Repository Layer (FlightRepository, WeatherRepository)
    ↓
Data Sources (Room DB, Retrofit API, SharedPreferences, Sensors)
```

**Dependency Injection:** Koin 4.0.0 - all modules configured in `di/AppModule.kt`

## Key Packages

- `data/local/` - Room database (FlightSession entity, DAO)
- `data/weather/` - Retrofit service for Open-Meteo weather API
- `data/preferences/` - PreferencesManager wrapping SharedPreferences with StateFlow
- `sensor/` - GForceSensorManager for accelerometer with low-pass filtering
- `service/` - GForceOverlayService (foreground service with Compose overlay)
- `ui/screens/` - 12 screen implementations
- `ui/components/` - Reusable Compose components
- `viewmodel/` - State management for each feature
- `utils/` - TtsManager, FlightModeManager, extensions

## Non-Obvious Implementation Details

1. **G-Force Smoothing:** `GForceSensorManager` uses alpha=0.05 low-pass filter specifically tuned for turbulence detection sensitivity

2. **Flight Mode Restoration:** App checks for unfinished flight sessions within 24 hours on startup and offers to restore them

3. **Compose in Service:** `GForceOverlayService` renders a Compose-based overlay window on top of other apps

4. **Hardcoded Content:** Educational content in `data/AppContent.kt` is static (not fetched from API)

5. **Reactive Theme:** `PreferencesManager.themeModeFlow` provides real-time theme updates via StateFlow

## Tech Stack

- **UI:** Jetpack Compose (Sept 2024 BOM), Material Design 3
- **Navigation:** Navigation Compose 2.8.3
- **DI:** Koin 4.0.0
- **Database:** Room 2.6.1
- **Network:** Retrofit 2.9.0 + Gson
- **Location:** Google Play Services Location 21.0.1
- **Language:** Kotlin 2.0.21, Java 11 target
- **Min SDK:** 24, Target SDK: 36
