package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.model.Tool
import com.example.calmflight.ui.components.ScreenTitle
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.ToolsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel = koinViewModel(),
    onNavigateToTool: (String) -> Unit = {}
) {
    val tools by viewModel.tools.collectAsState()

    Scaffold(
        topBar = {
            StandardTopBar(
                titleRes = R.string.nav_tools,
                onBackClick = null
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(tools) { tool ->
                ToolCard(tool, onClick = { onNavigateToTool(tool.id) })
            }
        }
    }
}

@Composable
fun ToolCard(tool: Tool, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealSoft.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(tool.iconName) {
                        "Breathing" -> Icons.Default.Air
                        "Headphones" -> Icons.Default.Headphones
                        "Graph" -> Icons.Default.MonitorHeart
                        "Wave" -> Icons.Default.Waves
                        "Clock" -> Icons.Default.AccessTime
                        "Trophy" -> Icons.Default.EmojiEvents
                        "Cloud" -> Icons.Default.Cloud
                        "Chart" -> Icons.Default.DateRange
                        "Shield" -> Icons.Default.Shield
                        "Meditation" -> Icons.Default.SelfImprovement
                        else -> Icons.Default.QuestionMark
                    }, 
                    contentDescription = null,
                    tint = TealSoft
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = tool.name,
                style = MaterialTheme.typography.titleSmall,
                color = BeigeWarm,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = BeigeWarm.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
