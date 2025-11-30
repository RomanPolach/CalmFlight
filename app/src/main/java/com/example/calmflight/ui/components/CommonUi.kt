package com.example.calmflight.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.example.calmflight.R
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            ScreenTitle(
                text = title,
                color = BeigeWarm,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.previous),
                        tint = BeigeWarm
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyDeep
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    titleRes: Int,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    StandardTopBar(
        title = stringResource(titleRes),
        onBackClick = onBackClick,
        actions = actions,
        modifier = modifier
    )
}

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
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * Converts HTML string to AnnotatedString for Compose Text
 * Handles basic HTML tags like <b>, <i>, <br>, etc.
 */
@Composable
fun htmlToAnnotatedString(html: String): AnnotatedString {
    return remember(html) {
        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        buildAnnotatedString {
            // Use the CharSequence directly to preserve newlines
            val text = spanned as CharSequence
            append(text)
            
            // Apply bold styling
            spanned.getSpans(0, spanned.length, android.text.style.StyleSpan::class.java).forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)
                if ((span.style and android.graphics.Typeface.BOLD) != 0) {
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold, color = TealSoft),
                        start = start,
                        end = end
                    )
                } else if (span.style == android.graphics.Typeface.ITALIC) {
                    addStyle(
                        style = SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        start = start,
                        end = end
                    )
                }
            }
        }
    }
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
            .padding(12.dp)
    ) {
        // Check if text contains HTML tags
        val containsHtml = text.contains("<b>") || text.contains("<i>") || 
                          text.contains("<br>") || text.contains("</")
        
        if (containsHtml) {
            // Render as HTML
            Text(
                text = htmlToAnnotatedString(text),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    lineHeight = 30.sp
                ),
                color = BeigeWarm,
                textAlign = TextAlign.Start
            )
        } else {
            // Render as plain text
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

@Composable
fun ImageWithTitle(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = BeigeWarm,
    containerColor: Color = NavyLight
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image with 16:9 aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Title text
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ImageWithTitle(
    imageRes: Int,
    titleRes: Int,
    modifier: Modifier = Modifier,
    titleColor: Color = BeigeWarm,
    containerColor: Color = NavyLight
) {
    ImageWithTitle(
        imageRes = imageRes,
        title = stringResource(titleRes),
        modifier = modifier,
        titleColor = titleColor,
        containerColor = containerColor
    )
}
