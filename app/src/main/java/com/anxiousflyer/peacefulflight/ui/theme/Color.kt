package com.anxiousflyer.peacefulflight.ui.theme

import androidx.compose.ui.graphics.Color

// CalmFlight Palette
val NavyDeep = Color(0xFF0A192F)
val NavyLight = Color(0xFF112240)
val NavyLighter = Color(0xFF233554)

val BlueLight = Color(0xFFD5E0F3)

val TealSoft = Color(0xFF64FFDA)
val TealDark = Color(0xFF14B8A6)

val BeigeWarm = Color(0xFFF4F1EA)
val BeigeDim = Color(0xFF8892B0)

val OrangeSafe = Color(0xFFFFAB00) // For errors/alerts instead of red

// Material Mapping
val Primary = TealSoft
val OnPrimary = NavyDeep
val PrimaryContainer = NavyLighter
val OnPrimaryContainer = TealSoft

val Secondary = BeigeWarm
val OnSecondary = NavyDeep
val SecondaryContainer = NavyLighter
val OnSecondaryContainer = BeigeWarm

val Background = NavyDeep
val OnBackground = BeigeWarm

val Surface = NavyLight
val OnSurface = BeigeWarm

val Error = OrangeSafe
val OnError = NavyDeep

// Light Theme Palette
val CloudWhite = Color(0xFFF5F7FA) // Soft airy background
val PureWhite = Color(0xFFFFFFFF)  // Card surfaces
val TealDeep = Color(0xFF0F766E)   // Primary action color (readable on light)
val SlateDark = Color(0xFF1E293B)  // Main text
val SlateMedium = Color(0xFF64748B) // Secondary text
val SkySoft = Color(0xFFE0F2FE)    // Secondary containers
val SkyMist = Color(0xFFECF4F9)    // Subtle tinted surface for toolbars/nav bars
