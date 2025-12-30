package com.anxiousflyer.peacefulflight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
import com.anxiousflyer.peacefulflight.ui.MainScreen
import com.anxiousflyer.peacefulflight.ui.theme.PeacefulFlightTheme
import com.anxiousflyer.peacefulflight.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            val darkTheme = when (themeMode) {
                PreferencesManager.ThemeMode.LIGHT -> false
                PreferencesManager.ThemeMode.DARK -> true
                PreferencesManager.ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            PeacefulFlightTheme(darkTheme = darkTheme) {
                MainScreen()
            }
        }
    }
}
