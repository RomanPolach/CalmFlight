package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.AnxietyRatingBar
import com.example.calmflight.ui.components.ContentCard
import com.example.calmflight.ui.components.PrimaryButton
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.RidingTheWaveViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RidingTheWaveScreen(
    viewModel: RidingTheWaveViewModel = koinViewModel(),
    onFinish: () -> Unit
) {
    val currentTextRes by viewModel.currentTextRes.collectAsState()
    val isLastStep by viewModel.isLastStep.collectAsState()
    val anxietyScore by viewModel.anxietyScore.collectAsState()
    val feedbackRes by viewModel.feedbackMessageRes.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val currentStepIndex by viewModel.currentStepIndex.collectAsState()
    val scrollState = rememberScrollState()

    // Helper to get current text string for TTS
    val currentText = stringResource(currentTextRes)
    
    // Auto-play logic: When text changes, notify ViewModel to potentially speak it
    LaunchedEffect(currentText) {
        viewModel.onStepContentChanged(currentText)
    }
    
    // Auto-scroll to top when step changes
    LaunchedEffect(currentStepIndex) {
        scrollState.animateScrollTo(0)
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog(onFinish) },
            confirmButton = {
                TextButton(onClick = { viewModel.closeDialog(onFinish) }) {
                    Text(stringResource(R.string.close_btn), color = TealSoft)
                }
            },
            title = { Text(stringResource(R.string.congrats_title), color = BeigeWarm) },
            text = { Text(stringResource(R.string.congrats_msg), color = BeigeWarm) },
            containerColor = NavyLight,
            titleContentColor = BeigeWarm,
            textContentColor = BeigeWarm
        )
    }

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(R.string.rtw2_title),
                onBackClick = onFinish
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // TTS Button
                    IconButton(
                        onClick = { viewModel.toggleTts(currentText) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(NavyLight, shape = MaterialTheme.shapes.medium)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Read aloud",
                            tint = TealSoft,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            
            ContentCard(text = currentText)

            Spacer(modifier = Modifier.height(20.dp))

            if (!isLastStep) {
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }

        // Interactive Rating Footer
        AnxietyRatingBar(
            rating = anxietyScore,
            onRatingChanged = { viewModel.updateAnxietyScore(it) },
            onSubmitRating = { viewModel.submitRating() },
            onFinish = { viewModel.finishSession(onFinish) },
            feedbackMessageRes = feedbackRes
        )
        }
    }
}
