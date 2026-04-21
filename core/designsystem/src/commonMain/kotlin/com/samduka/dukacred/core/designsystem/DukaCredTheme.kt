package com.samduka.dukacred.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary          = DukaCredColors.ForestGreen400,
    onPrimary        = DukaCredColors.Cream100,
    primaryContainer = DukaCredColors.ForestGreen700,
    secondary        = DukaCredColors.Ochre400,
    onSecondary      = DukaCredColors.Charcoal900,
    background       = DukaCredColors.ForestGreen900,
    onBackground     = DukaCredColors.Cream100,
    surface          = DukaCredColors.ForestGreen800,
    onSurface        = DukaCredColors.Cream100,
    surfaceVariant   = DukaCredColors.ForestGreen700,
    onSurfaceVariant = DukaCredColors.Cream200,
    outline          = DukaCredColors.ForestGreen600,
    error            = DukaCredColors.Error,
    onError          = DukaCredColors.Cream100,
)

private val LightColorScheme = lightColorScheme(
    primary          = DukaCredColors.ForestGreen500,
    onPrimary        = DukaCredColors.Cream100,
    primaryContainer = DukaCredColors.ForestGreen300,
    secondary        = DukaCredColors.Ochre500,
    onSecondary      = DukaCredColors.Cream100,
    background       = DukaCredColors.Cream100,
    onBackground     = DukaCredColors.ForestGreen900,
    surface          = DukaCredColors.Cream200,
    onSurface        = DukaCredColors.ForestGreen900,
    surfaceVariant   = DukaCredColors.Cream300,
    onSurfaceVariant = DukaCredColors.ForestGreen700,
    outline          = DukaCredColors.Cream300,
    error            = DukaCredColors.Error,
    onError          = DukaCredColors.Cream100,
)

@Composable
fun DukaCredTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val sora   = DukaCredFonts.soraFamily()
    val dmSans = DukaCredFonts.dmSansFamily()

    val typography = Typography(
        // Display — hero text on screens
        displayLarge = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Bold,
            fontSize   = 48.sp,
            lineHeight = 56.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Bold,
            fontSize   = 36.sp,
            lineHeight = 44.sp,
        ),
        // Headlines — screen titles
        headlineLarge = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 28.sp,
            lineHeight = 36.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 24.sp,
            lineHeight = 32.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 20.sp,
            lineHeight = 28.sp,
        ),
        // Titles — card headers
        titleLarge = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Medium,
            fontSize   = 18.sp,
            lineHeight = 26.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = sora,
            fontWeight = FontWeight.Medium,
            fontSize   = 16.sp,
            lineHeight = 24.sp,
        ),
        // Body — all readable content
        bodyLarge = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.Normal,
            fontSize   = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.Normal,
            fontSize   = 14.sp,
            lineHeight = 22.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.Normal,
            fontSize   = 12.sp,
            lineHeight = 18.sp,
        ),
        // Labels — buttons, chips, captions
        labelLarge = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp,
            lineHeight = 20.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.Medium,
            fontSize   = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = dmSans,
            fontWeight = FontWeight.Medium,
            fontSize   = 11.sp,
            lineHeight = 16.sp,
        ),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content,
    )
}