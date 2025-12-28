package com.anxiousflyer.peacefulflight.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.data.AppContent
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager
import com.anxiousflyer.peacefulflight.model.FlightStatus
import com.anxiousflyer.peacefulflight.ui.components.GForceMonitorCard
import com.anxiousflyer.peacefulflight.ui.components.StandardTopBar
import com.anxiousflyer.peacefulflight.ui.components.WeatherWidget
import com.anxiousflyer.peacefulflight.viewmodel.CockpitViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CockpitScreen(
    viewModel: CockpitViewModel = koinViewModel(),
    onNavigateToSos: () -> Unit,
    onNavigateToTool: (String) -> Unit,
    onNavigateToLearn: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFlightActive by viewModel.isFlightActive.collectAsState()

    // Inject preferences manager for the dialog
    val preferencesManager: PreferencesManager = org.koin.compose.koinInject()

    // Refresh weather when screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.refreshWeather()
    }
    
    val phases = remember {
        listOf(
            FlightStatus.BOARDING,
            FlightStatus.TAKEOFF,
            FlightStatus.CRUISE,
            FlightStatus.LANDING
        )
    }
    
    val pagerState = rememberPagerState(pageCount = { phases.size })
    val scope = rememberCoroutineScope()
    
    // Sync viewmodel -> pager (initial or external change)
    LaunchedEffect(uiState.status) {
        val index = phases.indexOf(uiState.status)
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    // Sync pager -> viewmodel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setStatus(phases[pagerState.currentPage])
    }

    // Show settings dialog
    if (uiState.showSettingsDialog) {
        com.anxiousflyer.peacefulflight.ui.components.SettingsDialog(
            preferencesManager = preferencesManager,
            onDismiss = { viewModel.toggleSettingsDialog(false) }
        )
    }

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.cockpit_title),
                onBackClick = null,
                actions = {
                    IconButton(onClick = { viewModel.toggleSettingsDialog(true) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Phase Tabs
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            phases.forEachIndexed { index, phase ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { 
                        Text(
                            text = stringResource(phase.labelRes).uppercase(),
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }

        // Main Content Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val currentPhase = phases[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                when (currentPhase) {
                    FlightStatus.BOARDING -> BoardingContent(
                        onNavigateToTool = onNavigateToTool,
                        weatherState = uiState.weather,
                        onFetchWeather = viewModel::fetchWeather,
                        onLocationError = viewModel::setWeatherError,
                        onRetry = viewModel::refreshWeather,
                        isFlightActive = isFlightActive,
                        isMetric = uiState.isMetric
                    )
                    FlightStatus.TAKEOFF -> TakeoffContent(
                        onNavigateToTool = onNavigateToTool,
                        onNavigateToLearn = onNavigateToLearn
                    )
                    FlightStatus.CRUISE -> CruiseContent(
                        onNavigateToTool = onNavigateToTool,
                        onNavigateToLearn = onNavigateToLearn
                    )
                    FlightStatus.LANDING -> LandingContent(
                        onNavigateToTool = onNavigateToTool,
                        onNavigateToLearn = onNavigateToLearn
                    )
                    else -> {}
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        }
    }
}

@Composable
fun BoardingContent(
    onNavigateToTool: (String) -> Unit,
    weatherState: com.anxiousflyer.peacefulflight.model.WeatherUiState?,
    onFetchWeather: (Double, Double) -> Unit,
    onLocationError: (Int) -> Unit,
    onRetry: () -> Unit,
    isFlightActive: Boolean,
    isMetric: Boolean
) {
    // 1. Weather Widget
    WeatherWidget(
        weatherState = weatherState,
        onFetchWeather = onFetchWeather,
        onLocationError = onLocationError,
        onRetry = onRetry,
        isMetric = isMetric
    )
    
    // 2. OnLandCard ("Ready to fly?")
    OnLandCard(isFlightActive = isFlightActive)
}

@Composable
fun TakeoffContent(
    onNavigateToTool: (String) -> Unit,
    onNavigateToLearn: (String) -> Unit
) {
    // G-Force Monitor
    GForceMonitorCard(isCompact = true, onClick = { onNavigateToTool("3") })
    
    Text(
        text = stringResource(R.string.takeoff_tools_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    // Tool: Riding the Wave
    ToolShortcutCard(
        title = stringResource(R.string.rtw2_title),
        description = stringResource(R.string.tool_shortcut_desc_rtw),
        icon = Icons.Default.GraphicEq,
        onClick = { onNavigateToTool("5") }
    )
    
    // Learn Questions Section
    LearnQuestionsSection(
        sectionId = "takeoff",
        titleRes = R.string.learn_section_takeoff,
        onNavigateToLearn = onNavigateToLearn
    )
}

@Composable
fun CruiseContent(
    onNavigateToTool: (String) -> Unit,
    onNavigateToLearn: (String) -> Unit
) {
    
    // G-Force Monitor (Always good to have)
    GForceMonitorCard(isCompact = true, onClick = { onNavigateToTool("3") })
    
    Text(
        text = stringResource(R.string.cruise_tools_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    // Tool: Facing the Fear
    ToolShortcutCard(
        title = stringResource(R.string.ftf_title),
        description = stringResource(R.string.tool_shortcut_desc_ftf),
        icon = Icons.Default.ArrowForward,
        onClick = { onNavigateToTool("8") }
    )
    
    // Learn Questions Section
    LearnQuestionsSection(
        sectionId = "flight",
        titleRes = R.string.learn_section_flight,
        onNavigateToLearn = onNavigateToLearn
    )
}

@Composable
fun LandingContent(
    onNavigateToTool: (String) -> Unit,
    onNavigateToLearn: (String) -> Unit
) {

    Text(
        text = stringResource(R.string.landing_tools_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    // Tool: Riding the Wave
    ToolShortcutCard(
        title = stringResource(R.string.rtw2_title),
        description = stringResource(R.string.tool_shortcut_desc_rtw_landing),
        icon = Icons.Default.GraphicEq,
        onClick = { onNavigateToTool("5") }
    )
    
    // Learn Questions Section
    LearnQuestionsSection(
        sectionId = "landing",
        titleRes = R.string.learn_section_landing,
        onNavigateToLearn = onNavigateToLearn
    )
}

@Composable
fun ToolShortcutCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun OnLandCard(isFlightActive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    if (isFlightActive) R.string.on_land_card_title_active 
                    else R.string.on_land_card_title
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(
                    if (isFlightActive) R.string.on_land_card_desc_active 
                    else R.string.on_land_card_desc
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun LearnQuestionsSection(
    sectionId: String,
    titleRes: Int,
    onNavigateToLearn: (String) -> Unit
) {
    val section = AppContent.learnSections.find { it.id == sectionId }
    
    if (section != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    section.items.forEachIndexed { index, item ->
                        QuestionListItem(
                            question = stringResource(item.questionRes),
                            onClick = { onNavigateToLearn(item.id) }
                        )

                        if (index < section.items.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionListItem(
    question: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = question,
            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}
