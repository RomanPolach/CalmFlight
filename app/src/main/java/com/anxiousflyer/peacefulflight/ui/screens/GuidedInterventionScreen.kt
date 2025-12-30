package com.anxiousflyer.peacefulflight.ui.screens

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
import androidx.compose.material3.Surface
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
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.ui.components.ContentCard
import com.anxiousflyer.peacefulflight.ui.components.PrimaryButton
import com.anxiousflyer.peacefulflight.ui.components.StandardTopBar
import com.anxiousflyer.peacefulflight.viewmodel.GuidedInterventionViewModel
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

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Handle empty initial state safely
    if (uiState.currentTextRes == 0) return

    val currentText = stringResource(uiState.currentTextRes)
    
    // Auto-scroll to top when step changes
    LaunchedEffect(uiState.currentStepIndex) {
        scrollState.animateScrollTo(0)
    }

    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog(onFinish) },
            confirmButton = {
                TextButton(onClick = { viewModel.closeDialog(onFinish) }) {
                    Text(
                        stringResource(R.string.close_btn),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            title = {
                Text(
                    stringResource(R.string.congrats_title),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    stringResource(R.string.congrats_msg),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Scaffold(
        topBar = {
            StandardTopBar(
                title = stringResource(titleRes),
                onBackClick = onFinish
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 4.dp
                ) {
                    IconButton(
                        onClick = { viewModel.toggleTts(currentText) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isAutoPlayEnabled) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Read aloud",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            ContentCard(text = currentText)

            Spacer(modifier = Modifier.height(20.dp))

            if (!uiState.isLastStep) {
                val nextStepIndex = uiState.currentStepIndex + 1
                val nextStepText = if (nextStepIndex < steps.size) stringResource(steps[nextStepIndex]) else ""
                
                PrimaryButton(
                    text = stringResource(R.string.continue_btn),
                    onClick = { viewModel.nextStep(nextStepText) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            } else {
                // Finish button on last step
                PrimaryButton(
                    text = stringResource(R.string.finish_btn),
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
        }
    }
}


