package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Identidade Visual CitizenTech
val NavyPrimary = Color(0xFF12313D)
val NavySecondary = Color(0xFF1B4651)
val TealPrimary = Color(0xFF087F78)
val TealDark = Color(0xFF066861)
val MintContainer = Color(0xFFE3F3EF)
val PaperBackground = Color(0xFFF4F7F5)
val WhiteSurface = Color(0xFFFFFFFF)
val InkText = Color(0xFF18333B)
val MutedText = Color(0xFF667B7E)
val LineBorder = Color(0xFFDCE7E3)

val AmberWarning = Color(0xFFA45D08)
val AmberContainer = Color(0xFFFFF4DF)
val AmberBorder = Color(0xFFF1DFBD)
val AmberAccent = Color(0xFFD27B29)

val RedCritical = Color(0xFFA83E39)
val RedContainer = Color(0xFFFFF0EE)

val GreenStatus = Color(0xFF176C4D)
val GreenContainer = Color(0xFFE8F5ED)
val MintMeter = Color(0xFF78C8AD)
val AmberMeter = Color(0xFFF0B35D)

// Alto Contraste (acessibilidade)
val HighContrastNavy = Color(0xFF001E2B)
val HighContrastTeal = Color(0xFF005B55)
val HighContrastTealDark = Color(0xFF004E49)
val HighContrastMint = Color(0xFFD1FFF2)
val HighContrastInk = Color(0xFF000000)
val HighContrastMuted = Color(0xFF303B3B)
val HighContrastBorder = Color(0xFF172D2D)

@Immutable
data class CitizenTechPalette(
    val navy: Color,
    val navySecondary: Color,
    val teal: Color,
    val tealDark: Color,
    val mint: Color,
    val paper: Color,
    val cardSurface: Color,
    val ink: Color,
    val muted: Color,
    val border: Color,
    val amber: Color,
    val amberBg: Color,
    val amberBorder: Color,
    val red: Color,
    val redBg: Color,
    val green: Color,
    val greenBg: Color,
    val infoNoteBg: Color,
    val isHighContrast: Boolean,
)

val DefaultCitizenTechPalette = CitizenTechPalette(
    navy = NavyPrimary,
    navySecondary = NavySecondary,
    teal = TealPrimary,
    tealDark = TealDark,
    mint = MintContainer,
    paper = PaperBackground,
    cardSurface = WhiteSurface,
    ink = InkText,
    muted = MutedText,
    border = LineBorder,
    amber = AmberWarning,
    amberBg = AmberContainer,
    amberBorder = AmberBorder,
    red = RedCritical,
    redBg = RedContainer,
    green = GreenStatus,
    greenBg = GreenContainer,
    infoNoteBg = Color(0xFFF0F5F3),
    isHighContrast = false,
)

val HighContrastCitizenTechPalette = CitizenTechPalette(
    navy = HighContrastNavy,
    navySecondary = HighContrastNavy,
    teal = HighContrastTeal,
    tealDark = HighContrastTealDark,
    mint = HighContrastMint,
    paper = WhiteSurface,
    cardSurface = WhiteSurface,
    ink = HighContrastInk,
    muted = HighContrastMuted,
    border = HighContrastBorder,
    amber = Color(0xFF7A4100),
    amberBg = AmberContainer,
    amberBorder = HighContrastBorder,
    red = Color(0xFF8C1D18),
    redBg = RedContainer,
    green = Color(0xFF0E4F37),
    greenBg = GreenContainer,
    infoNoteBg = Color(0xFFE7EEEC),
    isHighContrast = true,
)

val LocalCitizenTechColors = staticCompositionLocalOf { DefaultCitizenTechPalette }
