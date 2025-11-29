package com.example.calmflight.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyFactsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.safety_title),
                onBackClick = onBack
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subtitle
            Text(
                text = stringResource(R.string.safety_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = BeigeWarm.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Key Statistics - 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.safety_stat_passengers_title),
                    description = stringResource(R.string.safety_stat_passengers_desc),
                    color = TealSoft,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.safety_stat_flights_title),
                    description = stringResource(R.string.safety_stat_flights_desc),
                    color = OrangeSafe,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.safety_stat_daily_title),
                    description = stringResource(R.string.safety_stat_daily_desc),
                    color = Color(0xFF9C27B0), // Purple
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.safety_stat_accident_rate_title),
                    description = stringResource(R.string.safety_stat_accident_rate_desc),
                    color = Color(0xFF4CAF50), // Green
                    modifier = Modifier.weight(1f)
                )
            }

            // Visual Comparison Graph
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.safety_comparison_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = BeigeWarm,
                        fontWeight = FontWeight.Bold
                    )

                    // Massive visual comparison
                    ComparisonGraph()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem2(
                            color = TealSoft,
                            label = stringResource(R.string.safety_comparison_flights)
                        )
                        LegendItem2(
                            color = Color(0xFFFF5252), // Red for accidents
                            label = stringResource(R.string.safety_comparison_accidents)
                        )
                    }
                }
            }

            // More Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.safety_stat_fatal_title),
                    description = stringResource(R.string.safety_stat_fatal_desc),
                    color = Color(0xFFFF9800), // Orange
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.safety_stat_improvement_title),
                    description = stringResource(R.string.safety_stat_improvement_desc),
                    color = Color(0xFF2196F3), // Blue
                    modifier = Modifier.weight(1f)
                )
            }

            // Big reassuring fact
            Card(
                colors = CardDefaults.cardColors(containerColor = TealSoft.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.safety_years_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = TealSoft,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.safety_years_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = BeigeWarm,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom message
            Text(
                text = stringResource(R.string.safety_bottom_message),
                style = MaterialTheme.typography.bodyLarge,
                color = BeigeWarm,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = BeigeWarm,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ComparisonGraph() {
    // Visual representation: 40M flights vs 45 accidents
    // We'll show a massive bar for flights and a tiny one for accidents
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth * 0.35f
        val gap = canvasWidth * 0.1f

        // Flights bar (almost full height)
        val flightsHeight = canvasHeight * 0.95f
        drawRoundRect(
            color = TealSoft,
            topLeft = Offset(gap, canvasHeight - flightsHeight),
            size = Size(barWidth, flightsHeight),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Accidents bar (tiny - 0.0001% of flights = 45/40,000,000)
        // Make it visible at 2% height for visual clarity
        val accidentsHeight = canvasHeight * 0.02f
        drawRoundRect(
            color = Color(0xFFFF5252),
            topLeft = Offset(gap * 2 + barWidth, canvasHeight - accidentsHeight),
            size = Size(barWidth, accidentsHeight),
            cornerRadius = CornerRadius(8.dp.toPx())
        )
    }
}

@Composable
private fun LegendItem2(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = BeigeWarm
        )
    }
}



