package com.samduka.dukacred.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.samduka.dukacred.core.designsystem.generated.resources.Res
import com.samduka.dukacred.core.designsystem.generated.resources.*
import org.jetbrains.compose.resources.Font

/**
 * `dukacred.core.designsystem.generated.resources` is the auto-generated
 * Compose Resources accessor for `core:designsystem`'s
 * `composeResources/font/` directory — regenerated on every Gradle sync once
 * the three files from the asset checklist are in place.
 */
@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, weight = FontWeight.Normal),
    Font(Res.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(Res.font.inter_bold, weight = FontWeight.Bold),
)

/** Opt-in tabular-figure feature so digits in ledgers/balances align vertically. */
fun TextStyle.asTabularNums(): TextStyle = copy(
    fontFeatureSettings = "tnum",
)

/**
 * Non-M3 style bucket mirroring the YAML `typography:` block 1:1
 * (`display`, `headline-lg`, `headline-lg-mobile`, `title-md`, `body-lg`,
 * `body-sm`, `label-caps`, `data-tabular`). Reach these via
 * `MaterialTheme.dukaTypography` for anything that doesn't cleanly map onto
 * an M3 `Typography` slot (e.g. `labelCaps`, `dataTabular`).
 */
@Immutable
data class DukaTypography(
    val display: TextStyle,
    val headlineLarge: TextStyle,
    val headlineLargeMobile: TextStyle,
    val titleMedium: TextStyle,
    val bodyLarge: TextStyle,
    val bodySmall: TextStyle,
    val labelCaps: TextStyle,
    val dataTabular: TextStyle,
)

@Composable
fun buildDukaTypography(): DukaTypography {
    val inter = interFontFamily()
    return DukaTypography(
        display = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.02).em(36),
        ),
        headlineLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.01).em(28),
        ),
        headlineLargeMobile = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        labelCaps = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.05.em(12),
        ),
        dataTabular = TextStyle(
            fontFamily = inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp,
        ).asTabularNums(),
    )
}

/** Converts an `em` multiplier (as used by the YAML `letterSpacing` values) into `sp` for a given font size. */
private fun Double.em(fontSizeSp: Int): androidx.compose.ui.unit.TextUnit = (this * fontSizeSp).sp

@Composable
fun buildDukaMaterialTypography(dukaType: DukaTypography): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = dukaType.display,
        displayMedium = dukaType.display.copy(fontSize = 32.sp, lineHeight = 40.sp),
        headlineLarge = dukaType.headlineLarge,
        headlineMedium = dukaType.headlineLargeMobile,
        titleLarge = dukaType.titleMedium.copy(fontSize = 20.sp, lineHeight = 26.sp),
        titleMedium = dukaType.titleMedium,
        titleSmall = dukaType.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = dukaType.bodyLarge,
        bodyMedium = dukaType.bodySmall.copy(fontSize = 15.sp, lineHeight = 22.sp),
        bodySmall = dukaType.bodySmall,
        labelLarge = dukaType.labelCaps.copy(letterSpacing = 0.02.em(14), fontSize = 14.sp, lineHeight = 20.sp),
        labelMedium = dukaType.labelCaps,
        labelSmall = dukaType.labelCaps.copy(fontSize = 11.sp, lineHeight = 14.sp),
    )
}

val LocalDukaTypography = staticCompositionLocalOf<DukaTypography> {
    error("LocalDukaTypography not provided — wrap content in DukaCredTheme")
}