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
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.utils.answer

@Composable
fun LearnDetailScreen(
    item: LearnItem,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TealSoft
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Question
            ScreenTitle(
                text = stringResource(item.questionRes),
                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Answer Card using ContentCard for consistent styling
            ContentCard(text = item.answer())

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
