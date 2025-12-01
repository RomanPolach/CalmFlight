package com.anxiousflyer.peacefulflight.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// We prioritize Dark Theme for CalmFlight as requested
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error,
    onError = OnError,
    surfaceContainer = NavyLighter
)

// Modern Light Theme
private val LightColorScheme = lightColorScheme(
    primary = TealDeep,
    onPrimary = PureWhite,
    primaryContainer = SkySoft,
    onPrimaryContainer = TealDeep,
    secondary = SlateMedium,
    onSecondary = PureWhite,
    secondaryContainer = SkyMist, // Subtle tinted background
    onSecondaryContainer = SlateDark,
    background = SkyMist, // Subtle tinted background instead of white
    onBackground = SlateDark,
    surface = SkyMist, // Subtle tinted surface for toolbars/nav
    onSurface = SlateDark,
    surfaceVariant = SkySoft,
    onSurfaceVariant = SlateDark,
    error = OrangeSafe,
    onError = PureWhite,
    surfaceContainer = PureWhite // Pure white for elevated cards to create depth
)

@Composable
fun PeacefulFlightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // turning off dynamic color to enforce our branding
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
