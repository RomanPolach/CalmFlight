package com.example.calmflight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmflight.R
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import kotlin.math.roundToInt

@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TealSoft,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
fun ContentCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NavyLight.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                lineHeight = 30.sp
            ),
            color = BeigeWarm,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = TealSoft, contentColor = NavyDeep),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = BeigeWarm.copy(alpha = 0.2f),
            contentColor = BeigeWarm
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnxietyRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    onSubmitRating: () -> Unit,
    onFinish: () -> Unit,
    feedbackMessageRes: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(NavyLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.anxiety_level_label, rating.roundToInt()),
            color = BeigeWarm,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = rating,
            onValueChange = onRatingChanged,
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = TealSoft,
                activeTrackColor = TealSoft,
                inactiveTrackColor = BeigeWarm.copy(alpha = 0.3f)
            )
        )

        // Feedback Area
        if (feedbackMessageRes != null) {
            Text(
                text = stringResource(feedbackMessageRes),
                color = TealSoft,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.rate_btn),
                onClick = onSubmitRating,
                modifier = Modifier.weight(1f)
            )

            PrimaryButton(
                text = stringResource(R.string.finish_btn),
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
