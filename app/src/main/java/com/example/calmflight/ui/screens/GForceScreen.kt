package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.GForceExplanationCard
import com.example.calmflight.ui.components.GForceMonitorCard
import com.example.calmflight.ui.components.ScreenTitle
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep

@Composable
fun GForceScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp)
    ) {
        // Header with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.previous),
                    tint = BeigeWarm
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            ScreenTitle(
                text = stringResource(R.string.g_force_monitor),
                color = BeigeWarm,
                textAlign = androidx.compose.ui.text.style.TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Monitor Card
            GForceMonitorCard()
            
            // Explanation Card
            GForceExplanationCard()
        }
    }
}
