package com.example.calmflight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calmflight.R
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft

@Composable
fun FlightModeDialog(
    isStart: Boolean,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableFloatStateOf(5f) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(if (isStart) R.string.flight_start_title else R.string.flight_end_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TealSoft,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(if (isStart) R.string.flight_start_question else R.string.flight_end_question),
                    style = MaterialTheme.typography.bodyLarge,
                    color = BeigeWarm,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${rating.toInt()}/10",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TealSoft,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = TealSoft,
                        activeTrackColor = TealSoft,
                        inactiveTrackColor = BeigeWarm.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.cancel_btn),
                            color = BeigeWarm.copy(alpha = 0.7f)
                        )
                    }

                    Button(
                        onClick = { onConfirm(rating.toInt()) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealSoft, contentColor = NavyLight)
                    ) {
                        Text(
                            text = stringResource(if (isStart) R.string.start_flight_btn else R.string.end_flight_btn),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


