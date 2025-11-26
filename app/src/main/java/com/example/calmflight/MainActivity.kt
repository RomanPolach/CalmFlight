package com.example.calmflight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calmflight.ui.MainScreen
import com.example.calmflight.ui.theme.CalmFlightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalmFlightTheme {
                MainScreen()
            }
        }
    }
}
