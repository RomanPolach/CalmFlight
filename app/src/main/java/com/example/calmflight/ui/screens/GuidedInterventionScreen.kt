package com.example.calmflight.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.*
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import com.example.calmflight.viewmodel.GuidedInterventionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GuidedInterventionScreen(
    titleRes: Int,
    steps: List<Int>,
    viewModel: GuidedInterventionViewModel = koinViewModel(),
    onFinish: () -> Unit
) {
    // Initialize ViewModel with steps only once
    LaunchedEffect(Unit) {
        viewModel.initialize(steps)
    }

    val currentTextRes by viewModel.currentTextRes.collectAsState()
    val isLastStep by viewModel.isLastStep.collectAsState()
    val anxietyScore by viewModel.anxietyScore.collectAsState()
    val feedbackRes by viewModel.feedbackMessageRes.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val scrollState = rememberScrollState()
    
    // Handle empty initial state safely
    if (currentTextRes == 0) return

    val currentText = stringResource(currentTextRes)

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
                title = stringResource(titleRes),
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
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { viewModel.toggleTts(currentText) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(NavyLight, shape = MaterialTheme.shapes.medium)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
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
                val nextStepIndex = viewModel.currentStepIndex.collectAsState().value + 1
                val nextStepText = if (nextStepIndex < steps.size) stringResource(steps[nextStepIndex]) else ""
                
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    onClick = { viewModel.nextStep(nextStepText) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }

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


