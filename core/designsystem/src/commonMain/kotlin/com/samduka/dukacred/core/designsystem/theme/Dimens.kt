package com.samduka.dukacred.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 4dp-base spacing rhythm, matching YAML `spacing:`. Every layout gap should
 * be one of these — never a raw `.dp` literal — so the 8/16px rhythm called
 * out in the "Layout & Spacing" section stays enforced across the codebase.
 */
object DukaSpacing {
    val base: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val marginMobile: Dp = 16.dp
    val marginDesktop: Dp = 40.dp
    val gutter: Dp = 12.dp
}

/**
 * Tonal-layer elevation steps (Section "Elevation & Depth"). Dark mode leans
 * on `surfaceContainer*` tonal shifts rather than shadow depth; light mode
 * pairs the same steps with a soft ambient shadow.
 */
object DukaElevation {
    val level0: Dp = 0.dp // base background
    val level1: Dp = 1.dp // cards — subtle border, minimal/no shadow
    val level2: Dp = 3.dp // interactive/active surfaces
    val level3: Dp = 6.dp // modals, sheets, floating action button
}

/** Component sizing constants referenced throughout `Components.kt`. */
object DukaSize {
    val minTouchTarget: Dp = 48.dp // "All buttons have a minimum height of 48dp"
    val iconXs: Dp = 14.dp
    val iconSm: Dp = 16.dp
    val iconMd: Dp = 20.dp
    val iconLg: Dp = 24.dp
    val iconXl: Dp = 32.dp
    val borderHairline: Dp = 1.dp
    val borderFocus: Dp = 2.dp
    val avatarSm: Dp = 32.dp
    val avatarMd: Dp = 44.dp
    val bottomNavHeight: Dp = 64.dp
    val statusBadgeHeight: Dp = 24.dp
    val metricRingSize: Dp = 64.dp
    val metricRingStroke: Dp = 6.dp
}