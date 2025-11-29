package com.example.calmflight.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calmflight.R
import com.example.calmflight.ui.components.StandardTopBar
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft

@Composable
fun HelpOptionsScreen(
    onBack: () -> Unit,
    onNavigateToGForce: () -> Unit,
    onNavigateToRidingTheWave: () -> Unit,
    onNavigateToPostponeTheWorry: () -> Unit,
    onNavigateToWorryOlympics: () -> Unit,
    onNavigateToFacingTheFear: () -> Unit,
    onNavigateToRealityCheck: () -> Unit,
    onNavigateToSafetyFacts: () -> Unit,
    onNavigateToAcceptanceMeditation: () -> Unit,
    onNavigateToSelfCompassion: () -> Unit
) {
    Scaffold(
        topBar = {
            StandardTopBar(
                titleRes = R.string.help_options_title,
                onBackClick = onBack
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_gforce_title),
                    description = stringResource(R.string.help_option_gforce_desc),
                    onClick = onNavigateToGForce
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_rtw_title),
                    description = stringResource(R.string.help_option_rtw_desc),
                    onClick = onNavigateToRidingTheWave
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_ptw_title),
                    description = stringResource(R.string.help_option_ptw_desc),
                    onClick = onNavigateToPostponeTheWorry
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_wo_title),
                    description = stringResource(R.string.help_option_wo_desc),
                    onClick = onNavigateToWorryOlympics
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_ftf_title),
                    description = stringResource(R.string.help_option_ftf_desc),
                    onClick = onNavigateToFacingTheFear
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_rc_title),
                    description = stringResource(R.string.help_option_rc_desc),
                    onClick = onNavigateToRealityCheck
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_sf_title),
                    description = stringResource(R.string.help_option_sf_desc),
                    onClick = onNavigateToSafetyFacts
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_am_title),
                    description = stringResource(R.string.help_option_am_desc),
                    onClick = onNavigateToAcceptanceMeditation
                )
            }
            item {
                HelpOptionCard(
                    title = stringResource(R.string.help_option_sca_title),
                    description = stringResource(R.string.help_option_sca_desc),
                    onClick = onNavigateToSelfCompassion
                )
            }
        }
    }
}

@Composable
private fun HelpOptionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TealSoft
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = BeigeWarm.copy(alpha = 0.8f)
            )
        }
    }
}
