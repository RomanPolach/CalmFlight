package com.example.calmflight.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.model.LearnItem
import com.example.calmflight.ui.components.ContentCard
import com.example.calmflight.ui.components.ImageWithTitle
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.utils.answer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnDetailScreen(
    item: LearnItem,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val headerHeight = 300.dp
    val headerHeightPx = with(density) { headerHeight.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(NavyDeep)
    ) {
        // ==========================================================
        // LAYER 1: THE HEADER IMAGE (Always visible initially)
        // ==========================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .graphicsLayer {
                    // Parallax + Fade out
                    translationY = -scrollState.value * 0.5f
                    alpha = (-1f / headerHeightPx) * scrollState.value + 1
                }
        ) {
            Image(
                // Directly using the resource, assuming it exists as requested
                painter = painterResource(id = R.drawable.airplane_toolbar),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, NavyDeep),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

                Text(
                    text = stringResource(item.questionRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
        }

        // ==========================================================
        // LAYER 2: THE SCROLLABLE CONTENT
        // ==========================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Transparent Spacer to show the image behind
            Spacer(Modifier.height(headerHeight))

            // The Content (Must have solid background)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep) // Important: Covers image when scrolling up
                    .padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                ContentCard(text = item.answer())

                // Optional bottom image
                if (item.imageRes != null && item.imageTitleRes != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    ImageWithTitle(imageRes = item.imageRes, titleRes = item.imageTitleRes)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // ==========================================================
        // LAYER 3: THE TOP BAR (Handles Status Bar)
        // ==========================================================
        val toolbarAlpha by remember {
            derivedStateOf {
                // Toolbar turns solid NavyDeep *before* the text hits the top
                (scrollState.value / (headerHeightPx * 0.7f)).coerceIn(0f, 1f)
            }
        }

        TopAppBar(
            title = {
                // Fade in title only when image is scrolled away
                if (scrollState.value > headerHeightPx - 100) {
                    Text(
                        text = stringResource(item.questionRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NavyDeep.copy(alpha = toolbarAlpha),
                scrolledContainerColor = NavyDeep
            ),
            // FIX: This allows the NavyDeep background to extend BEHIND the status bar
            windowInsets = WindowInsets.statusBars
        )
    }
}