package com.example.calmflight.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calmflight.R
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.calmflight.data.AppContent
import com.example.calmflight.ui.screens.CockpitScreen
import com.example.calmflight.ui.screens.GForceScreen
import com.example.calmflight.ui.screens.GuidedInterventionScreen
import com.example.calmflight.ui.screens.LearnDetailScreen
import com.example.calmflight.ui.screens.LearnScreen
import com.example.calmflight.ui.screens.RidingTheWaveScreen
import com.example.calmflight.ui.screens.SosScreen
import com.example.calmflight.ui.screens.ToolsScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = BottomNavItem.Cockpit.route, modifier = modifier) {
        composable(BottomNavItem.Cockpit.route) {
            CockpitScreen(
                onNavigateToSos = {
                    navController.navigate(BottomNavItem.Sos.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTool = { toolId ->
                    when (toolId) {
                        "3" -> navController.navigate(Screen.GForceMonitorStandalone.route)
                        "5" -> navController.navigate(Screen.RidingTheWave.route)
                        "6" -> navController.navigate(Screen.PostponeTheWorry.route)
                        "7" -> navController.navigate(Screen.WorryOlympics.route)
                        "8" -> navController.navigate(Screen.FacingTheFear.route)
                    }
                },
                onNavigateToLearn = { itemId ->
                    navController.navigate(Screen.LearnDetail.createRoute(itemId))
                }
            )
        }
        composable(BottomNavItem.Learn.route) {
            LearnScreen(
                onNavigateToDetail = { itemId ->
                    navController.navigate(Screen.LearnDetail.createRoute(itemId))
                }
            )
        }
        composable(
            route = Screen.LearnDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            val item = AppContent.learnSections.flatMap { it.items }.find { it.id == itemId }
            if (item != null) {
                LearnDetailScreen(
                    item = item,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(BottomNavItem.Sos.route) {
            SosScreen(
                onNavigateToPanic = {
                    navController.navigate(Screen.RidingTheWave.route)
                },
                onNavigateToTurbulence = {
                    navController.navigate(Screen.GForceMonitorStandalone.route)
                },
                onExitSos = {
                    navController.navigate(BottomNavItem.Cockpit.route) {
                        popUpTo(BottomNavItem.Cockpit.route) { inclusive = true }
                    }
                }
            )
        }
        composable(BottomNavItem.Tools.route) {
            ToolsScreen(
                onNavigateToTool = { toolId ->
                    when (toolId) {
                        "3" -> navController.navigate(Screen.GForceMonitorStandalone.route)
                        "5" -> navController.navigate(Screen.RidingTheWave.route)
                        "6" -> navController.navigate(Screen.PostponeTheWorry.route)
                        "7" -> navController.navigate(Screen.WorryOlympics.route)
                        "8" -> navController.navigate(Screen.FacingTheFear.route)
                        "9" -> navController.navigate(Screen.RealityCheck.route)
                        "10" -> navController.navigate(Screen.SafetyFacts.route)
                        "11" -> navController.navigate(Screen.AcceptanceMeditation.route)
                    }
                }
            )
        }
        composable(Screen.RidingTheWave.route) {
            RidingTheWaveScreen(
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.GForceMonitorStandalone.route) {
            GForceScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.PostponeTheWorry.route) {
            val steps = listOf(
                R.string.ptw_intro,
                R.string.ptw_step_1,
                R.string.ptw_step_2,
                R.string.ptw_step_3,
                R.string.ptw_step_4,
                R.string.ptw_step_5,
                R.string.ptw_step_6
            )
            GuidedInterventionScreen(
                titleRes = R.string.ptw_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.WorryOlympics.route) {
            val steps = listOf(
                R.string.wo_intro,
                R.string.wo_step_1,
                R.string.wo_step_2,
                R.string.wo_step_3,
                R.string.wo_step_4,
                R.string.wo_step_5
            )
            GuidedInterventionScreen(
                titleRes = R.string.wo_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.FacingTheFear.route) {
            val steps = listOf(
                R.string.ftf_intro,
                R.string.ftf_step_1,
                R.string.ftf_step_2,
                R.string.ftf_step_3,
                R.string.ftf_step_4,
                R.string.ftf_step_5,
                R.string.ftf_step_6,
                R.string.ftf_step_7,
                R.string.ftf_step_8
            )
            GuidedInterventionScreen(
                titleRes = R.string.ftf_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.RealityCheck.route) {
            com.example.calmflight.ui.screens.RealityCheckScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SafetyFacts.route) {
            com.example.calmflight.ui.screens.SafetyFactsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AcceptanceMeditation.route) {
            val steps = listOf(
                R.string.am_intro,
                R.string.am_step_1,
                R.string.am_step_2,
                R.string.am_step_3,
                R.string.am_step_4,
                R.string.am_step_5,
                R.string.am_step_6
            )
            GuidedInterventionScreen(
                titleRes = R.string.am_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
    }
}
