package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

val StudyHubLightColorScheme = lightColorScheme(
    primary = BrandTeal,
    onPrimary = Color.White,
    primaryContainer = BrandPaleTeal,
    onPrimaryContainer = BrandDeepTeal,
    secondary = BrandBlue,
    onSecondary = Color.White,
    secondaryContainer = BrandLightBlue,
    onSecondaryContainer = BrandBlue,
    tertiary = BrandDeepTeal,
    onTertiary = Color.White,
    background = AppBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = CardSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderSubtleLight,
    outlineVariant = Color(0xFFE8EEF5),
    error = StatusDanger,
    onError = Color.White
)

val StudyHubDarkColorScheme = darkColorScheme(
    primary = BrandTealDark,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = BrandPaleTealDark,
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = BrandBlueDark,
    onSecondary = Color.White,
    secondaryContainer = BrandLightBlueDark,
    onSecondaryContainer = Color(0xFFBFDBFE),
    tertiary = BrandTealDark,
    onTertiary = Color.White,
    background = AppBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = CardSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderSubtleDark,
    outlineVariant = Color(0xFF1A2942),
    error = StatusDanger,
    onError = Color.White
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

// Convenient semantic properties
val ColorScheme.textPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = onBackground

val ColorScheme.textSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = onSurfaceVariant

val ColorScheme.cardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = outline

val ColorScheme.cardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = surface

val ColorScheme.chipBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = surfaceVariant

@Composable
fun StudyHubTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) StudyHubDarkColorScheme else StudyHubLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
