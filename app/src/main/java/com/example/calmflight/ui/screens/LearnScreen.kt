package com.example.calmflight.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.model.LearnItem
import com.example.calmflight.model.LearnSection
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.LearnViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LearnScreen(
    viewModel: LearnViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit = {}
) {
    val sections by viewModel.sections.collectAsState()
    val expandedSection by viewModel.expandedSection.collectAsState()

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.nav_learn)
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sections) { section ->
                SectionCard(
                    section = section,
                    isExpanded = expandedSection == section.id,
                    onToggle = { viewModel.toggleSection(section.id) },
                    onItemClick = onNavigateToDetail
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    section: LearnSection,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(section.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = BeigeWarm,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TealSoft
                )
            }

            if (isExpanded) {
                HorizontalDivider(
                    color = BeigeWarm.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                section.items.forEach { item ->
                    LearnItemRow(item = item, onClick = { onItemClick(item.id) })
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LearnItemRow(
    item: LearnItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(item.questionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = BeigeWarm,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TealSoft
        )
    }
}
