package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val displayName: String, val description: String) {
    LIGHT("Light", "Bright & serene daytime reading"),
    DARK("Dark", "Peaceful night mode for contemplation"),
    SPIRITUAL_PAPER("Spiritual Paper", "Sacred warm parchment & Vedic manuscript tone")
}

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimaryDark,
    onPrimary = Color(0xFF4A1400),
    primaryContainer = Color(0xFF5A1E00),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = DivineGoldMuted,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = Color(0xFF452E00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = Color(0xFFFFB4A9),
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF5C473C),
    outlineVariant = Color(0xFF3B2D26)
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF380D00),
    secondary = SandalwoodSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDF9E),
    onSecondaryContainer = Color(0xFF261900),
    tertiary = GoldTertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF7ECE1),
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFF867266),
    outlineVariant = Color(0xFFD8C7BC)
)

private val SpiritualPaperColorScheme = lightColorScheme(
    primary = PaperPrimary,
    onPrimary = Color(0xFFFFF7ED),
    primaryContainer = PaperPrimaryContainer,
    onPrimaryContainer = PaperOnPrimaryContainer,
    secondary = PaperSecondary,
    onSecondary = Color(0xFFFFF8EC),
    secondaryContainer = Color(0xFFE8D3B8),
    onSecondaryContainer = Color(0xFF321A04),
    tertiary = PaperTertiary,
    background = PaperBackground,
    surface = PaperSurface,
    surfaceVariant = PaperSurfaceVariant,
    onBackground = PaperTextPrimary,
    onSurface = PaperTextPrimary,
    onSurfaceVariant = PaperTextSecondary,
    outline = Color(0xFF8D735B),
    outlineVariant = Color(0xFFC7B39B)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SPIRITUAL_PAPER,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.SPIRITUAL_PAPER -> SpiritualPaperColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
