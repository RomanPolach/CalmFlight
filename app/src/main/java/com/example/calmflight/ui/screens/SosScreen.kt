package com.example.calmflight.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmflight.R
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.OrangeSafe
import com.example.calmflight.ui.theme.TealSoft

@Composable
fun SosScreen(
    onNavigateToPanic: () -> Unit,
    onNavigateToTurbulence: () -> Unit,
    onExitSos: () -> Unit
) {
    // Subtle background pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        // Background Pulse Element
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TealSoft.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onExitSos,
                    modifier = Modifier
                        .background(NavyLight.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_btn),
                        tint = BeigeWarm
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Header Question
            Text(
                text = stringResource(R.string.sos_help_message),
                style = MaterialTheme.typography.headlineSmall,
                color = BeigeWarm.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Option 1: Panic Attack (Internal)
            SosOptionCard(
                title = stringResource(R.string.sos_panic_title),
                subtitle = stringResource(R.string.sos_panic_subtitle),
                color = TealSoft,
                onClick = onNavigateToPanic
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option 2: Turbulence/Fear (External)
            SosOptionCard(
                title = stringResource(R.string.sos_turbulence_title),
                subtitle = stringResource(R.string.sos_turbulence_subtitle),
                color = OrangeSafe,
                onClick = onNavigateToTurbulence
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SosOptionCard(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored Indicator Bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    lineHeight = 32.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = BeigeWarm.copy(alpha = 0.7f)
                )
            }
        }
    }
}
