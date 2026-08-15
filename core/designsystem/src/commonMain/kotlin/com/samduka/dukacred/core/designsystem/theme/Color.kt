package com.samduka.dukacred.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Raw hex tokens lifted 1:1 from the "Merchant Trust" design-token YAML.
 * Nothing here should be referenced directly from UI code — go through
 * [DukaCredDarkColorScheme] / [DukaCredLightColorScheme] (Material slots) or
 * [LocalDukaColors] (semantic extras) instead.
 */
private object RawDark {
    val surface = Color(0xFF131313)
    val surfaceDim = Color(0xFF131313)
    val surfaceBright = Color(0xFF393939)
    val surfaceContainerLowest = Color(0xFF0E0E0E)
    val surfaceContainerLow = Color(0xFF1C1B1B)
    val surfaceContainer = Color(0xFF20201F)
    val surfaceContainerHigh = Color(0xFF2A2A2A)
    val surfaceContainerHighest = Color(0xFF353535)
    val onSurface = Color(0xFFE5E2E1)
    val onSurfaceVariant = Color(0xFFC1C8C2)
    val inverseSurface = Color(0xFFE5E2E1)
    val inverseOnSurface = Color(0xFF313030)
    val outline = Color(0xFF8B938C)
    val outlineVariant = Color(0xFF414943)
    val surfaceTint = Color(0xFFA2D1B7)
    val primary = Color(0xFFA2D1B7)
    val onPrimary = Color(0xFF083825)
    val primaryContainer = Color(0xFF013220)
    val onPrimaryContainer = Color(0xFF6F9C84)
    val inversePrimary = Color(0xFF3B6751)
    val secondary = Color(0xFFC7C7BC)
    val onSecondary = Color(0xFF303129)
    val secondaryContainer = Color(0xFF494A41)
    val onSecondaryContainer = Color(0xFFB9B9AE)
    val tertiary = Color(0xFFE9C349)
    val onTertiary = Color(0xFF3C2F00)
    val tertiaryContainer = Color(0xFFCBA72F)
    val onTertiaryContainer = Color(0xFF4E3D00)
    val error = Color(0xFFFFB4AB)
    val onError = Color(0xFF690005)
    val errorContainer = Color(0xFF93000A)
    val onErrorContainer = Color(0xFFFFDAD6)
    val primaryFixed = Color(0xFFBDEDD2)
    val primaryFixedDim = Color(0xFFA2D1B7)
    val onPrimaryFixed = Color(0xFF002113)
    val onPrimaryFixedVariant = Color(0xFF234F3B)
    val background = Color(0xFF131313)
    val onBackground = Color(0xFFE5E2E1)
    val surfaceVariant = Color(0xFF353535)

    // Brand-spec extras (Section "Colors" of the YAML) not exposed as M3 slots.
    val brandPrimaryAnchor = Color(0xFF013220) // used for CTAs even in dark mode
    val gold = Color(0xFFD4AF37)
    val successEmerald = Color(0xFF2FB37C)
}

private object RawLight {
    val surface = Color(0xFFFCF9F8)
    val surfaceDim = Color(0xFFDCD9D9)
    val surfaceBright = Color(0xFFFCF9F8)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF6F3F2)
    val surfaceContainer = Color(0xFFF0EDED)
    val surfaceContainerHigh = Color(0xFFEAE7E7)
    val surfaceContainerHighest = Color(0xFFE5E2E1)
    val onSurface = Color(0xFF1C1B1B)
    val onSurfaceVariant = Color(0xFF414943)
    val inverseSurface = Color(0xFF313030)
    val inverseOnSurface = Color(0xFFF3F0EF)
    val outline = Color(0xFF717973)
    val outlineVariant = Color(0xFFC1C8C2)
    val surfaceTint = Color(0xFF3B6751)
    val primary = Color(0xFF001B0F)
    val onPrimary = Color(0xFFFFFFFF)
    val primaryContainer = Color(0xFF013220)
    val onPrimaryContainer = Color(0xFF6F9C84)
    val inversePrimary = Color(0xFFA2D1B7)
    val secondary = Color(0xFF5E5F56)
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFFE4E3D7)
    val onSecondaryContainer = Color(0xFF64655C)
    val tertiary = Color(0xFF735C00)
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFFCBA72F)
    val onTertiaryContainer = Color(0xFF4E3D00)
    val error = Color(0xFFBA1A1A)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF93000A)
    val primaryFixed = Color(0xFFBDEDD2)
    val primaryFixedDim = Color(0xFFA2D1B7)
    val onPrimaryFixed = Color(0xFF002113)
    val onPrimaryFixedVariant = Color(0xFF234F3B)
    val background = Color(0xFFFCF9F8)
    val onBackground = Color(0xFF1C1B1B)
    val surfaceVariant = Color(0xFFE5E2E1)

    val brandPrimaryAnchor = Color(0xFF013220)
    val gold = Color(0xFFD4AF37)
    val successEmerald = Color(0xFF1E8E5A)
}

val DukaCredDarkColorScheme = darkColorScheme(
    primary = RawDark.primary,
    onPrimary = RawDark.onPrimary,
    primaryContainer = RawDark.primaryContainer,
    onPrimaryContainer = RawDark.onPrimaryContainer,
    inversePrimary = RawDark.inversePrimary,
    secondary = RawDark.secondary,
    onSecondary = RawDark.onSecondary,
    secondaryContainer = RawDark.secondaryContainer,
    onSecondaryContainer = RawDark.onSecondaryContainer,
    tertiary = RawDark.tertiary,
    onTertiary = RawDark.onTertiary,
    tertiaryContainer = RawDark.tertiaryContainer,
    onTertiaryContainer = RawDark.onTertiaryContainer,
    background = RawDark.background,
    onBackground = RawDark.onBackground,
    surface = RawDark.surface,
    onSurface = RawDark.onSurface,
    surfaceVariant = RawDark.surfaceVariant,
    onSurfaceVariant = RawDark.onSurfaceVariant,
    surfaceTint = RawDark.surfaceTint,
    inverseSurface = RawDark.inverseSurface,
    inverseOnSurface = RawDark.inverseOnSurface,
    error = RawDark.error,
    onError = RawDark.onError,
    errorContainer = RawDark.errorContainer,
    onErrorContainer = RawDark.onErrorContainer,
    outline = RawDark.outline,
    outlineVariant = RawDark.outlineVariant,
    surfaceContainerLowest = RawDark.surfaceContainerLowest,
    surfaceContainerLow = RawDark.surfaceContainerLow,
    surfaceContainer = RawDark.surfaceContainer,
    surfaceContainerHigh = RawDark.surfaceContainerHigh,
    surfaceContainerHighest = RawDark.surfaceContainerHighest,
    surfaceBright = RawDark.surfaceBright,
    surfaceDim = RawDark.surfaceDim,
)

val DukaCredLightColorScheme = lightColorScheme(
    primary = RawLight.primary,
    onPrimary = RawLight.onPrimary,
    primaryContainer = RawLight.primaryContainer,
    onPrimaryContainer = RawLight.onPrimaryContainer,
    inversePrimary = RawLight.inversePrimary,
    secondary = RawLight.secondary,
    onSecondary = RawLight.onSecondary,
    secondaryContainer = RawLight.secondaryContainer,
    onSecondaryContainer = RawLight.onSecondaryContainer,
    tertiary = RawLight.tertiary,
    onTertiary = RawLight.onTertiary,
    tertiaryContainer = RawLight.tertiaryContainer,
    onTertiaryContainer = RawLight.onTertiaryContainer,
    background = RawLight.background,
    onBackground = RawLight.onBackground,
    surface = RawLight.surface,
    onSurface = RawLight.onSurface,
    surfaceVariant = RawLight.surfaceVariant,
    onSurfaceVariant = RawLight.onSurfaceVariant,
    surfaceTint = RawLight.surfaceTint,
    inverseSurface = RawLight.inverseSurface,
    inverseOnSurface = RawLight.inverseOnSurface,
    error = RawLight.error,
    onError = RawLight.onError,
    errorContainer = RawLight.errorContainer,
    onErrorContainer = RawLight.onErrorContainer,
    outline = RawLight.outline,
    outlineVariant = RawLight.outlineVariant,
    surfaceContainerLowest = RawLight.surfaceContainerLowest,
    surfaceContainerLow = RawLight.surfaceContainerLow,
    surfaceContainer = RawLight.surfaceContainer,
    surfaceContainerHigh = RawLight.surfaceContainerHigh,
    surfaceContainerHighest = RawLight.surfaceContainerHighest,
    surfaceBright = RawLight.surfaceBright,
    surfaceDim = RawLight.surfaceDim,
)

/**
 * Semantic tokens the design spec calls out that don't map to a Material 3
 * color-scheme slot: chart series colors, status-chip tints, skeleton-loader
 * gradient stops, and hairline separators. Access via `MaterialTheme.dukaColors`
 * once inside [com.samduka.dukacred.core.designsystem.theme.DukaCredTheme].
 */
@Immutable
data class DukaExtendedColors(
    // Interactive charts (Section "Components — Interactive Charts")
    val chartSales: Color,
    val chartExpenses: Color,
    val chartGridLine: Color,
    // Status chips (Section "Components — Status Chips")
    val successBg: Color,
    val successOn: Color,
    val warningBg: Color,
    val warningOn: Color,
    val neutralBg: Color,
    val neutralOn: Color,
    val errorBg: Color,
    val errorOn: Color,
    // Misc
    val goldHighlight: Color,
    val hairline: Color,
    val skeletonBase: Color,
    val skeletonHighlight: Color,
    val scannerGuide: Color,
)

val DukaExtendedDarkColors = DukaExtendedColors(
    chartSales = RawDark.primary,
    chartExpenses = RawDark.gold,
    chartGridLine = RawDark.outlineVariant.copy(alpha = 0.24f),
    successBg = RawDark.primaryContainer,
    successOn = RawDark.primary,
    warningBg = RawDark.tertiaryContainer.copy(alpha = 0.24f),
    warningOn = RawDark.tertiary,
    neutralBg = RawDark.surfaceContainerHigh,
    neutralOn = RawDark.onSurfaceVariant,
    errorBg = RawDark.errorContainer,
    errorOn = RawDark.onErrorContainer,
    goldHighlight = RawDark.gold,
    hairline = RawDark.outlineVariant.copy(alpha = 0.4f),
    skeletonBase = RawDark.surfaceContainerHigh,
    skeletonHighlight = RawDark.surfaceContainerHighest,
    scannerGuide = RawDark.primary,
)

val DukaExtendedLightColors = DukaExtendedColors(
    chartSales = RawLight.brandPrimaryAnchor,
    chartExpenses = RawLight.gold,
    chartGridLine = RawLight.outlineVariant.copy(alpha = 0.5f),
    successBg = RawLight.primaryContainer.copy(alpha = 0.12f),
    successOn = RawLight.brandPrimaryAnchor,
    warningBg = RawLight.gold.copy(alpha = 0.16f),
    warningOn = RawLight.tertiary,
    neutralBg = RawLight.surfaceContainerHigh,
    neutralOn = RawLight.onSurfaceVariant,
    errorBg = RawLight.errorContainer,
    errorOn = RawLight.onErrorContainer,
    goldHighlight = RawLight.gold,
    hairline = RawLight.outlineVariant.copy(alpha = 0.6f),
    skeletonBase = RawLight.surfaceContainerHigh,
    skeletonHighlight = RawLight.surfaceContainerLowest,
    scannerGuide = RawLight.successEmerald,
)

val LocalDukaColors = staticCompositionLocalOf { DukaExtendedLightColors }