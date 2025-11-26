package com.example.calmflight.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmflight.R
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import kotlin.math.sqrt
import kotlinx.coroutines.delay

@Composable
fun GForceMonitorCard(
    showExplanation: Boolean = false
) {
    val gForce = rememberGForceSensor()
    // Keep history of last 300 points for the graph (approx 6 seconds at 50Hz)
    val history = remember { mutableStateListOf<Float>() }
    
    // Track min/max readings while screen is open
    val minReading = remember { mutableFloatStateOf(1.0f) }
    val maxReading = remember { mutableFloatStateOf(1.0f) }
    var initialized = remember { mutableStateOf(false) }
    
    // Displayed value - updated on a timer for stability
    val displayedGForce = remember { mutableFloatStateOf(1.0f) }
    
    // Timer to update displayed value every 500ms
    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            if (history.isNotEmpty()) {
                val recentCount = minOf(history.size, 10)
                val recentHistory = history.takeLast(recentCount)
                
                // Find the most extreme value (furthest from 1.0G) in the recent history
                val maxDeviation = recentHistory.maxByOrNull { kotlin.math.abs(it - 1.0f) } ?: 1.0f
                
                displayedGForce.floatValue = maxDeviation
            }
        }
    }
    
    // Status stability - track recent statuses and pick the most common one
    val stableStatus = remember { mutableStateOf(GForceStatus.SMOOTH) }
    val recentStatuses = remember { mutableStateListOf<GForceStatus>() }
    val maxRecentStatuses = 50 // About 2-3 seconds of readings
    
    // Update history and min/max whenever gForce changes
    LaunchedEffect(gForce.value) {
        val currentG = gForce.value
        history.add(currentG)
        // Keep 300 points for a longer history view
        if (history.size > 300) {
            history.removeAt(0)
        }
        
        // Update min/max
        if (!initialized.value) {
            minReading.floatValue = currentG
            maxReading.floatValue = currentG
            initialized.value = true
        } else {
            if (currentG < minReading.floatValue) {
                minReading.floatValue = currentG
            }
            if (currentG > maxReading.floatValue) {
                maxReading.floatValue = currentG
            }
        }
        
        val deviation = kotlin.math.abs(currentG - 1.0f)
        val newStatus = when {
            deviation <= 0.03f -> GForceStatus.SMOOTH
            deviation <= 0.07f -> GForceStatus.LIGHT_BUMPS
            deviation <= 0.13f -> GForceStatus.MODERATE
            else -> GForceStatus.BUMPY
        }
        
        // Track recent statuses
        recentStatuses.add(newStatus)
        if (recentStatuses.size > maxRecentStatuses) {
            recentStatuses.removeAt(0)
        }
        
        if (recentStatuses.size >= 10) {
            val bumpyCount = recentStatuses.count { it == GForceStatus.BUMPY }
            val moderateCount = recentStatuses.count { it == GForceStatus.MODERATE }
            val lightCount = recentStatuses.count { it == GForceStatus.LIGHT_BUMPS }
            
            stableStatus.value = when {
                bumpyCount >= 4 -> GForceStatus.BUMPY 
                moderateCount >= 6 -> GForceStatus.MODERATE
                lightCount >= 10 -> GForceStatus.LIGHT_BUMPS 
                else -> GForceStatus.SMOOTH
            }
        }
    }

    val (statusText, statusColor) = when (stableStatus.value) {
        GForceStatus.SMOOTH -> stringResource(R.string.status_smooth) to TealSoft
        GForceStatus.LIGHT_BUMPS -> stringResource(R.string.status_light_bumps) to TealSoft
        GForceStatus.MODERATE -> stringResource(R.string.status_moderate) to com.example.calmflight.ui.theme.OrangeSafe
        GForceStatus.BUMPY -> stringResource(R.string.status_bumpy) to com.example.calmflight.ui.theme.OrangeSafe
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.g_force_monitor),
                    style = MaterialTheme.typography.labelMedium,
                    color = BeigeWarm.copy(alpha = 0.7f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.gforce_current_label) + ": ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BeigeWarm.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.2f G", displayedGForce.floatValue),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TealSoft,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Min/Max readings row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.gforce_min_label) + ": ",
                        style = MaterialTheme.typography.bodySmall,
                        color = BeigeWarm.copy(alpha = 0.5f)
                    )
                    Text(
                        text = String.format("%.2f G", minReading.floatValue),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TealSoft.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.gforce_max_label) + ": ",
                        style = MaterialTheme.typography.bodySmall,
                        color = BeigeWarm.copy(alpha = 0.5f)
                    )
                    Text(
                        text = String.format("%.2f G", maxReading.floatValue),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TealSoft.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                // Real-time Graph
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Map Y axis: 0.0G -> Height, 3.0G -> 0 (extend to show unsafe zone)
                    val mapY = { g: Float ->
                        val clamped = g.coerceIn(0f, 3.0f)
                        height - (clamped / 3.0f * height)
                    }

                    // Draw UNSAFE ZONE (above 2.5G) - Red/Orange tint
                    val unsafeZoneTop = mapY(3.0f)
                    val unsafeZoneBottom = mapY(2.5f)
                    drawRect(
                        color = Color(0xFFFF6B6B).copy(alpha = 0.15f), // Light red for unsafe zone
                        topLeft = Offset(0f, unsafeZoneTop),
                        size = androidx.compose.ui.geometry.Size(width, unsafeZoneBottom - unsafeZoneTop)
                    )
                    
                    // Draw SAFE ZONE (0 to 2.5G) - Green/Teal tint - WIDER and more visible
                    val safeZoneTop = mapY(2.5f)
                    val safeZoneBottom = mapY(0f)
                    drawRect(
                        color = TealSoft.copy(alpha = 0.2f), // More visible safe zone
                        topLeft = Offset(0f, safeZoneTop),
                        size = androidx.compose.ui.geometry.Size(width, safeZoneBottom - safeZoneTop)
                    )
                    
                    // Draw "normal" zone highlight (0.8 to 1.2G) - Even more visible
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                TealSoft.copy(alpha = 0.1f),
                                TealSoft.copy(alpha = 0.3f),
                                TealSoft.copy(alpha = 0.3f),
                                TealSoft.copy(alpha = 0.1f)
                            ),
                            startY = mapY(1.2f),
                            endY = mapY(0.8f)
                        )
                    )
                    
                    // Draw boundary line at 2.5G (start of unsafe zone) - CLEARLY VISIBLE
                    drawLine(
                        color = Color(0xFFFF6B6B).copy(alpha = 0.6f), // Red line for unsafe boundary
                        start = Offset(0f, mapY(2.5f)),
                        end = Offset(width, mapY(2.5f)),
                        strokeWidth = 3.dp.toPx()
                    )
                    
                    // Draw reference lines
                    // 1.0G center line - more visible
                    drawLine(
                        color = TealSoft.copy(alpha = 0.5f),
                        start = Offset(0f, mapY(1.0f)),
                        end = Offset(width, mapY(1.0f)),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw the history path
                    if (history.isNotEmpty()) {
                        val path = Path()
                        // Scale X to fit 300 points across the width
                        val stepX = width / 300f
                        
                        history.forEachIndexed { index, g ->
                            val x = width - ((history.size - 1 - index) * stepX)
                            val y = mapY(g)
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = TealSoft,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
                
                // Labels on graph
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "3.0G",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF6B6B).copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "2.5G",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF6B6B).copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = stringResource(R.string.safe_operating_zone),
                    color = TealSoft.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Status text - now stable and readable
            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineSmall,
                color = statusColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Safe range - BIGGER and BOLD
            Text(
                text = stringResource(R.string.gforce_safe_range),
                style = MaterialTheme.typography.titleMedium,
                color = TealSoft,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun GForceExplanationCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyLight.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.gforce_explanation_title),
                style = MaterialTheme.typography.titleMedium,
                color = TealSoft,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stringResource(R.string.gforce_explanation),
                style = MaterialTheme.typography.bodyMedium,
                color = BeigeWarm.copy(alpha = 0.9f),
                lineHeight = 24.sp
            )
        }
    }
}

private enum class GForceStatus {
    SMOOTH,
    LIGHT_BUMPS,
    MODERATE,
    BUMPY
}

@Composable
fun rememberGForceSensor(): State<Float> {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val gForce = remember { mutableFloatStateOf(1f) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            var smoothedValue = 9.81f
            // Alpha 0.05: "Goldilocks" smoothing.
            // 0.15 was too sensitive (picked up hand shakes).
            // 0.03 was too slow (missed real bumps).
            // 0.05 filters out high-freq jitter but catches the "heave" of turbulence.
            val alpha = 0.05f

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    
                    // Calculate magnitude
                    val currentRaw = sqrt((x*x + y*y + z*z).toDouble()).toFloat()
                    
                    // Apply Low-Pass Filter
                    smoothedValue = (currentRaw * alpha) + (smoothedValue * (1f - alpha))
                    
                    // Convert to G-Force
                    gForce.floatValue = smoothedValue / 9.81f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        accelerometer?.also {
            // Use SENSOR_DELAY_GAME for faster updates (approx 50Hz)
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME)
        }
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
    return gForce
}
