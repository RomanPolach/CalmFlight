package com.anxiousflyer.peacefulflight.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.ui.components.StandardTopBar
import com.anxiousflyer.peacefulflight.viewmodel.RealityCheckViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RealityCheckScreen(
    viewModel: RealityCheckViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val flights by viewModel.flightHistory.collectAsState()
    val avgDiff = viewModel.getAverageDifference(flights)

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.rc_title),
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Side padding only
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ScreenTitle moved to AppBar

            if (flights.isEmpty()) {
                EmptyStateCard()
            } else {
                // Insight Card - Dynamic height
                InsightCard(avgDiff)

                // Graph Section - Takes remaining space
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rc_flight_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)) {
                            AnxietyGraph(flights = flights)
                        }
                    }
                    
                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(
                            color = MaterialTheme.colorScheme.error,
                            label = stringResource(R.string.rc_legend_expected)
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        LegendItem(
                            color = MaterialTheme.colorScheme.primary,
                            label = stringResource(R.string.rc_legend_actual)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.rc_empty_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.rc_empty_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun InsightCard(avgDiff: Double, modifier: Modifier = Modifier) {
    val (title, message) = when {
        avgDiff > 1.5 -> stringResource(R.string.rc_insight_strong_title) to stringResource(R.string.rc_insight_strong_msg)
        avgDiff > 0.5 -> stringResource(R.string.rc_insight_surprised_title) to stringResource(R.string.rc_insight_surprised_msg)
        avgDiff >= -0.5 -> stringResource(R.string.rc_insight_realistic_title) to stringResource(R.string.rc_insight_realistic_msg)
        else -> stringResource(R.string.rc_insight_rough_title) to stringResource(R.string.rc_insight_rough_msg)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.15f
            )
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(), // Changed from fillMaxSize to wrap content naturally
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Section - Always visible now
            val (statsText, descText) = when {
                avgDiff > 0.1 -> stringResource(R.string.rc_avg_overestimate, avgDiff) to stringResource(R.string.rc_catastrophe_gap_desc)
                avgDiff < -0.1 -> stringResource(R.string.rc_avg_underestimate, Math.abs(avgDiff)) to stringResource(R.string.rc_reality_gap_desc)
                else -> stringResource(R.string.rc_avg_accurate) to stringResource(R.string.rc_reality_gap_desc)
            }

            Text(
                text = statsText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .padding(2.dp)
        ) {
             Canvas(modifier = Modifier.matchParentSize()) {
                 drawCircle(color = color)
             }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun AnxietyGraph(flights: List<com.anxiousflyer.peacefulflight.data.local.FlightSession>) {
    val maxScore = 10f
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary

    val beigeArgb = onSurfaceColor.toArgb()
    val orangeArgb = errorColor.toArgb()
    val tealArgb = primaryColor.toArgb()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = size.width / (flights.size * 3f + 1) // Space for 2 bars + spacing per item
        val groupWidth = barWidth * 3f
        val heightPerPoint = size.height / maxScore
        
        // Draw grid lines and labels
        for (i in 0..10 step 2) {
            val y = size.height - (i * heightPerPoint)
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.1f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            // Note: Text drawing requires native canvas in Compose Canvas 
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    i.toString(),
                    0f,
                    y,
                    android.graphics.Paint().apply {
                        color = beigeArgb
                        textSize = 24f
                        alpha = 100
                    }
                )
            }
        }

        flights.forEachIndexed { index, flight ->
            val xStart = index * groupWidth + barWidth / 2 + 30f // Offset for y-axis labels

            // Expected Bar (Error color)
            val expectedHeight = flight.expectedFear * heightPerPoint
            drawRect(
                color = errorColor,
                topLeft = Offset(xStart, size.height - expectedHeight),
                size = Size(barWidth, expectedHeight)
            )

            // Value Label on top of Error bar
            drawContext.canvas.nativeCanvas.apply {
                 drawText(
                    flight.expectedFear.toString(),
                    xStart + barWidth/2 - 10f,
                    size.height - expectedHeight - 10f,
                    android.graphics.Paint().apply {
                        color = orangeArgb
                        textSize = 30f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                )
            }

            // Actual Bar (Primary color)
            val actualHeight = (flight.actualFear ?: 0) * heightPerPoint
            drawRect(
                color = primaryColor,
                topLeft = Offset(xStart + barWidth, size.height - actualHeight),
                size = Size(barWidth, actualHeight)
            )

            // Value Label on top of Primary bar
            drawContext.canvas.nativeCanvas.apply {
                 drawText(
                    (flight.actualFear ?: 0).toString(),
                    xStart + barWidth + barWidth/2 - 10f,
                    size.height - actualHeight - 10f,
                    android.graphics.Paint().apply {
                        color = tealArgb
                        textSize = 30f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                )
            }
        }
    }
}
