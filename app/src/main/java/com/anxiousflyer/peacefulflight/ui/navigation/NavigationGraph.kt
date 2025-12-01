package com.anxiousflyer.peacefulflight.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.data.AppContent
import com.anxiousflyer.peacefulflight.ui.screens.CockpitScreen
import com.anxiousflyer.peacefulflight.ui.screens.GForceScreen
import com.anxiousflyer.peacefulflight.ui.screens.GuidedInterventionScreen
import com.anxiousflyer.peacefulflight.ui.screens.LearnDetailScreen
import com.anxiousflyer.peacefulflight.ui.screens.LearnScreen
import com.anxiousflyer.peacefulflight.ui.screens.RidingTheWaveScreen
import com.anxiousflyer.peacefulflight.ui.screens.SosScreen
import com.anxiousflyer.peacefulflight.ui.screens.ToolsScreen

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
                        "13" -> navController.navigate(Screen.CatastrophicThinking.route)
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
                onNavigateToHelpOptions = {
                    navController.navigate(Screen.HelpOptions.route)
                },
                onExitSos = {
                    navController.navigate(BottomNavItem.Cockpit.route) {
                        popUpTo(BottomNavItem.Cockpit.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.HelpOptions.route) {
            com.anxiousflyer.peacefulflight.ui.screens.HelpOptionsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToGForce = { navController.navigate(Screen.GForceMonitorStandalone.route) },
                onNavigateToRidingTheWave = { navController.navigate(Screen.RidingTheWave.route) },
                onNavigateToPostponeTheWorry = { navController.navigate(Screen.PostponeTheWorry.route) },
                onNavigateToWorryOlympics = { navController.navigate(Screen.WorryOlympics.route) },
                onNavigateToFacingTheFear = { navController.navigate(Screen.FacingTheFear.route) },
                onNavigateToRealityCheck = { navController.navigate(Screen.RealityCheck.route) },
                onNavigateToSafetyFacts = { navController.navigate(Screen.SafetyFacts.route) },
                onNavigateToAcceptanceMeditation = { navController.navigate(Screen.AcceptanceMeditation.route) },
                onNavigateToSelfCompassion = { navController.navigate(Screen.SelfCompassion.route) }
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
                        "12" -> navController.navigate(Screen.SelfCompassion.route)
                        "13" -> navController.navigate(Screen.CatastrophicThinking.route)
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
                R.string.wo2_intro,
                R.string.wo2_step_1,
                R.string.wo2_step_2,
                R.string.wo2_step_3,
                R.string.wo2_step_4,
                R.string.wo2_step_5,
                R.string.wo2_step_6,
                R.string.wo2_step_7,
                R.string.wo2_step_8,
                R.string.wo2_step_9,
                R.string.wo2_step_10
            )
            GuidedInterventionScreen(
                titleRes = R.string.wo2_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.FacingTheFear.route) {
            val steps = listOf(
                R.string.cloud_meditation_part_1,
                R.string.cloud_meditation_part_2,
                R.string.cloud_meditation_part_3,
                R.string.cloud_meditation_part_4,
                R.string.cloud_meditation_part_5,
                R.string.cloud_meditation_part_6,
                R.string.cloud_meditation_part_7,
                R.string.cloud_meditation_part_8,
                R.string.cloud_meditation_part_9,
                R.string.cloud_meditation_footer
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
            com.anxiousflyer.peacefulflight.ui.screens.RealityCheckScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SafetyFacts.route) {
            com.anxiousflyer.peacefulflight.ui.screens.SafetyFactsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AcceptanceMeditation.route) {
            val steps = listOf(
                R.string.am_intro,
                R.string.am_part_1,
                R.string.am_part_2,
                R.string.am_part_3,
                R.string.am_part_4,
                R.string.am_part_5,
                R.string.am_part_6
            )
            GuidedInterventionScreen(
                titleRes = R.string.am_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SelfCompassion.route) {
            val steps = listOf(
                R.string.sca_intro,
                R.string.sca_step_1,
                R.string.sca_step_2,
                R.string.sca_step_3,
                R.string.sca_step_4,
                R.string.sca_step_5,
                R.string.sca_step_6,
                R.string.sca_step_7
            )
            GuidedInterventionScreen(
                titleRes = R.string.sca_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.CatastrophicThinking.route) {
            val steps = listOf(
                R.string.ct_investigation_part_1,
                R.string.ct_investigation_part_2,
                R.string.ct_investigation_part_3,
                R.string.ct_investigation_part_4,
                R.string.ct_investigation_part_5,
                R.string.ct_investigation_part_6,
                R.string.ct_investigation_part_7
            )
            GuidedInterventionScreen(
                titleRes = R.string.ct_title,
                steps = steps,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
    }
}
