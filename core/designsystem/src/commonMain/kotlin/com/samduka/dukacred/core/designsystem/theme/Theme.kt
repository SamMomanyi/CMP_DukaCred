package com.samduka.dukacred.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalDukaShapes = staticCompositionLocalOf { DukaShapes.Default }

/**
 * Root theme wrapper for every DukaCred screen (mobile + desktop).
 *
 * ```kotlin
 * DukaCredTheme {
 *     // screens...
 * }
 * ```
 *
 * Exposes three access points beyond the standard `MaterialTheme.colorScheme`
 * / `MaterialTheme.typography`:
 *  - `MaterialTheme.dukaColors`    → [DukaExtendedColors] (chart/status/skeleton tokens)
 *  - `MaterialTheme.dukaTypography` → [DukaTypography] (raw YAML type scale)
 *  - `MaterialTheme.dukaShapes`     → [DukaShapes] (raw YAML corner radii)
 *
 * System-bar theming (status/nav bar color + icon contrast) is intentionally
 * left to the app shell via [onSystemBarsColorChange] rather than baked in
 * here, since edge-to-edge/insets APIs differ across the Android, iOS, and
 * Desktop targets this module ships to.
 */
@Composable
fun DukaCredTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onSystemBarsColorChange: (isDarkTheme: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    onSystemBarsColorChange(darkTheme)

    val colorScheme = if (darkTheme) DukaCredDarkColorScheme else DukaCredLightColorScheme
    val extendedColors = if (darkTheme) DukaExtendedDarkColors else DukaExtendedLightColors
    val dukaTypography = buildDukaTypography()
    val materialTypography = buildDukaMaterialTypography(dukaTypography)

    CompositionLocalProvider(
        LocalDukaColors provides extendedColors,
        LocalDukaTypography provides dukaTypography,
        LocalDukaShapes provides DukaShapes.Default,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = materialTypography,
            shapes = DukaMaterialShapes,
            content = content,
        )
    }
}

/** `MaterialTheme.dukaColors` — semantic chart/status/skeleton tokens for the current theme. */
val MaterialTheme.dukaColors: DukaExtendedColors
    @Composable
    get() = LocalDukaColors.current

/** `MaterialTheme.dukaTypography` — raw YAML type scale (display, labelCaps, dataTabular, ...). */
val MaterialTheme.dukaTypography: DukaTypography
    @Composable
    get() = LocalDukaTypography.current

/** `MaterialTheme.dukaShapes` — raw YAML corner-radius tokens. */
val MaterialTheme.dukaShapes: DukaShapes
    @Composable
    get() = LocalDukaShapes.current