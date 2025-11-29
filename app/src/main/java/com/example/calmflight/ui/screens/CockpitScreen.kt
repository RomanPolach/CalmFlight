package com.example.calmflight.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.data.AppContent
import com.example.calmflight.model.FlightStatus
import com.example.calmflight.ui.components.GForceMonitorCard
import com.example.calmflight.ui.components.ScreenTitle
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.components.WeatherWidget
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.CockpitViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CockpitScreen(
    viewModel: CockpitViewModel = koinViewModel(),
    onNavigateToSos: () -> Unit,
    onNavigateToTool: (String) -> Unit,
    onNavigateToLearn: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFlightActive by viewModel.isFlightActive.collectAsState()
    
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

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.cockpit_title),
                onBackClick = null,
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings), tint = BeigeWarm)
                    }
                }
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Phase Tabs
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = TealSoft,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = TealSoft
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
                    selectedContentColor = TealSoft,
                    unselectedContentColor = BeigeWarm.copy(alpha = 0.5f)
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
                        isFlightActive = isFlightActive
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
    weatherState: com.example.calmflight.model.WeatherUiState?,
    onFetchWeather: (Double, Double) -> Unit,
    isFlightActive: Boolean
) {
    // 1. Weather Widget
    WeatherWidget(
        weatherState = weatherState,
        onFetchWeather = onFetchWeather
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
        color = BeigeWarm,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    // Tool: Riding the Wave
    ToolShortcutCard(
        title = stringResource(R.string.rtw_title),
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
        color = BeigeWarm,
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
    // G-Force Monitor
    GForceMonitorCard(isCompact = true, onClick = { onNavigateToTool("3") })
    
    Text(
        text = stringResource(R.string.landing_tools_title),
        style = MaterialTheme.typography.titleMedium,
        color = BeigeWarm,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    // Tool: Riding the Wave
    ToolShortcutCard(
        title = stringResource(R.string.rtw_title),
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
        colors = CardDefaults.cardColors(containerColor = NavyLight),
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
                    color = TealSoft,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BeigeWarm.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BeigeWarm.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SystemsStatusCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.cruise),
                contentDescription = stringResource(R.string.calm_flight_desc),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                NavyDeep.copy(alpha = 0.8f)
                            ),
                            startY = 0f
                        )
                    )
            )

            // Text Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.all_systems_normal),
                    style = MaterialTheme.typography.headlineSmall,
                    color = BeigeWarm,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.cruising_smoothly),
                    style = MaterialTheme.typography.bodyMedium,
                    color = BeigeWarm.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun OnLandCard(isFlightActive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyLight),
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
                color = TealSoft,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(
                    if (isFlightActive) R.string.on_land_card_desc_active 
                    else R.string.on_land_card_desc
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = BeigeWarm,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = TealSoft,
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
                color = BeigeWarm,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    section.items.forEach { item ->
                        QuestionListItem(
                            question = stringResource(item.questionRes),
                            onClick = { onNavigateToLearn(item.id) }
                        )
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
            style = MaterialTheme.typography.bodyMedium,
            color = BeigeWarm,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TealSoft.copy(alpha = 0.6f)
        )
    }
}
