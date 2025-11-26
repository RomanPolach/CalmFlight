package com.example.calmflight.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sos
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.calmflight.R

sealed class BottomNavItem(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Cockpit : BottomNavItem("cockpit", R.string.nav_cockpit, Icons.Default.Home)
    object Learn : BottomNavItem("learn", R.string.nav_learn, Icons.Default.School)
    object Sos : BottomNavItem("sos", R.string.nav_sos, Icons.Default.Sos)
    object Tools : BottomNavItem("tools", R.string.nav_tools, Icons.Default.Build)
}

sealed class Screen(val route: String) {
    object RidingTheWave : Screen("riding_the_wave")
    object GForceMonitorStandalone : Screen("g_force")
    object PostponeTheWorry : Screen("postpone_the_worry")
    object WorryOlympics : Screen("worry_olympics")
    object FacingTheFear : Screen("facing_the_fear")
    object LearnDetail : Screen("learn_detail/{itemId}") {
        fun createRoute(itemId: String) = "learn_detail/$itemId"
    }
}
