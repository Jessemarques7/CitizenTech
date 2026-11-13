package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val CitizenTechLightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = TealDark,
    secondary = NavyPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0ECEA),
    onSecondaryContainer = NavyPrimary,
    tertiary = AmberWarning,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFF73450B),
    error = RedCritical,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = RedCritical,
    background = PaperBackground,
    onBackground = InkText,
    surface = WhiteSurface,
    onSurface = InkText,
    surfaceVariant = Color(0xFFF0F5F3),
    onSurfaceVariant = MutedText,
    outline = LineBorder,
    outlineVariant = Color(0xFFE7EFEC),
)

private val CitizenTechHighContrastColorScheme = lightColorScheme(
    primary = HighContrastTeal,
    onPrimary = Color.White,
    primaryContainer = HighContrastMint,
    onPrimaryContainer = HighContrastNavy,
    secondary = HighContrastNavy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E6EA),
    onSecondaryContainer = HighContrastNavy,
    tertiary = Color(0xFF7A4100),
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFF4A2500),
    error = Color(0xFF8C1D18),
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = Color(0xFF5C0E0A),
    background = Color.White,
    onBackground = HighContrastInk,
    surface = Color.White,
    onSurface = HighContrastInk,
    surfaceVariant = Color(0xFFEBF2F0),
    onSurfaceVariant = HighContrastMuted,
    outline = HighContrastBorder,
    outlineVariant = HighContrastBorder,
)

val CitizenTechShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun CitizenTechTheme(
    highContrast: Boolean = false,
    userFontScale: Float = 1f,
    content: @Composable () -> Unit,
) {
    val palette = if (highContrast) HighContrastCitizenTechPalette else DefaultCitizenTechPalette
    val colorScheme = if (highContrast) CitizenTechHighContrastColorScheme else CitizenTechLightColorScheme
    val currentDensity = LocalDensity.current
    val scaledDensity = Density(
        density = currentDensity.density,
        fontScale = (currentDensity.fontScale * userFontScale).coerceIn(0.85f, 1.8f),
    )

    CompositionLocalProvider(
        LocalCitizenTechColors provides palette,
        LocalDensity provides scaledDensity,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CitizenTechTypography,
            shapes = CitizenTechShapes,
            content = content,
        )
    }
}
