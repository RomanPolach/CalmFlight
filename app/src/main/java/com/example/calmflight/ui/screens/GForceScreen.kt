package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.GForceExplanationCard
import com.example.calmflight.ui.components.GForceMonitorCard
import com.example.calmflight.ui.components.ScreenTitle
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep

@Composable
fun GForceScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.g_force_monitor),
                onBackClick = onBack
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Monitor Card
            GForceMonitorCard()
            
            // Explanation Card
            GForceExplanationCard()
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
