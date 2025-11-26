package com.example.calmflight.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calmflight.ui.navigation.BottomNavItem
import com.example.calmflight.ui.navigation.NavigationGraph
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.calmflight.ui.components.FlightModeDialog
import com.example.calmflight.R

import com.example.calmflight.ui.theme.OrangeSafe

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isFlightActive by viewModel.isFlightActive.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        FlightModeDialog(
            isStart = !isFlightActive,
            onConfirm = { rating ->
                if (isFlightActive) {
                    viewModel.endFlight(rating)
                } else {
                    viewModel.startFlight(rating)
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = NavyDeep,
                contentColor = BeigeWarm
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
                                selectedIconColor = NavyDeep,
                                selectedTextColor = TealSoft,
                                indicatorColor = TealSoft,
                                unselectedIconColor = BeigeWarm.copy(alpha = 0.6f),
                                unselectedTextColor = BeigeWarm.copy(alpha = 0.6f)
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = if (isFlightActive) OrangeSafe else NavyLight,
                contentColor = if (isFlightActive) NavyDeep else TealSoft,
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
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavigationGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
