package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmflight.model.LearnItem
import com.example.calmflight.ui.components.ContentCard
import com.example.calmflight.ui.components.ScreenTitle
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.utils.answer

@Composable
fun LearnDetailScreen(
    item: LearnItem,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(item.questionRes),
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Answer Card using ContentCard for consistent styling
            ContentCard(text = item.answer())

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
