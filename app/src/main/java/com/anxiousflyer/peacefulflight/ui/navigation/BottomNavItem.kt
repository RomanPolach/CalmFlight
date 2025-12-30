package com.anxiousflyer.peacefulflight.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import com.anxiousflyer.peacefulflight.R

sealed class BottomNavItem(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Cockpit : BottomNavItem("cockpit", R.string.nav_cockpit, Icons.Default.Home)
    object Learn : BottomNavItem("learn", R.string.nav_learn, Icons.Default.School)
    object Sos : BottomNavItem("sos", R.string.nav_sos, Icons.Default.MedicalServices)
    object Tools : BottomNavItem("tools", R.string.nav_tools, Icons.Default.Build)
}

sealed class Screen(val route: String) {
    object RidingTheWave : Screen("riding_the_wave")
    object GForceMonitorStandalone : Screen("g_force")
    object PostponeTheWorry : Screen("postpone_the_worry")
    object WorryOlympics : Screen("worry_olympics")
    object FacingTheFear : Screen("facing_the_fear")
    object RealityCheck : Screen("reality_check")
    object SafetyFacts : Screen("safety_facts")
    object AcceptanceMeditation : Screen("acceptance_meditation")
    object CatastrophicThinking : Screen("catastrophic_thinking")
    object LearnDetail : Screen("learn_detail/{itemId}") {
        fun createRoute(itemId: String) = "learn_detail/$itemId"
    }
    object HelpOptions : Screen("help_options")
    object SelfCompassion : Screen("self_compassion")
    object VoiceSettings : Screen("voice_settings")
    object Breathing : Screen("breathing")
}
