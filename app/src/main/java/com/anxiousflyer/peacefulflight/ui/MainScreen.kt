package com.anxiousflyer.peacefulflight.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.ui.components.FlightEndedConfirmationDialog
import com.anxiousflyer.peacefulflight.ui.components.FlightModeDialog
import com.anxiousflyer.peacefulflight.ui.navigation.BottomNavItem
import com.anxiousflyer.peacefulflight.ui.navigation.NavigationGraph
import com.anxiousflyer.peacefulflight.ui.navigation.Screen
import com.anxiousflyer.peacefulflight.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isFlightActive by viewModel.isFlightActive.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showFlightEndedConfirmation by remember { mutableStateOf(false) }
    
    if (showDialog) {
        FlightModeDialog(
            isStart = !isFlightActive,
            onConfirm = { rating ->
                if (isFlightActive) {
                    viewModel.endFlight(rating)
                    showFlightEndedConfirmation = true
                } else {
                    viewModel.startFlight(rating)
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    if (showFlightEndedConfirmation) {
        FlightEndedConfirmationDialog(
            onGoToRealityCheck = {
                showFlightEndedConfirmation = false
                navController.navigate(Screen.RealityCheck.route)
            },
            onDismiss = { showFlightEndedConfirmation = false }
        )
    }

    // Check if current route is a root tab screen
    val isRootTabScreen = currentRoute in listOf(
        BottomNavItem.Cockpit.route,
        BottomNavItem.Learn.route,
        BottomNavItem.Sos.route,
        BottomNavItem.Tools.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        //     contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isRootTabScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val items = listOf(
                        BottomNavItem.Cockpit,
                        BottomNavItem.Learn,
                        null, // Placeholder for FAB
                        BottomNavItem.Sos,
                        BottomNavItem.Tools
                    )
                    
                    items.forEach { item ->
                        if (item == null) {
                            // Placeholder for the FAB in the middle
                            NavigationBarItem(
                                selected = false,
                                onClick = { },
                                icon = { },
                                enabled = false
                            )
                        } else {
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                                label = { Text(stringResource(item.titleRes)) },
                                selected = currentRoute == item.route,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f
                                    ),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f
                                    )
                                ),
                                onClick = {
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (isRootTabScreen) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = if (isFlightActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = if (isFlightActive) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(72.dp)
                        .offset(y = 48.dp) // Push it down into the bottom bar gap
                ) {
                    Icon(
                        imageVector = if (isFlightActive) Icons.Default.FlightLand else Icons.Default.FlightTakeoff,
                        contentDescription = stringResource(if (isFlightActive) R.string.end_flight_btn else R.string.start_flight_btn),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        
        NavigationGraph(
            navController = navController,

        )
    }
}
