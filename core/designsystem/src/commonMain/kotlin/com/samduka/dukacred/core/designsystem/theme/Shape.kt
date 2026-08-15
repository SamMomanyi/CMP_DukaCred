package com.samduka.dukacred.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

/**
 * Corner-radius tokens matching YAML `rounded:`. "Soft" shape language per
 * the spec — sharp corners avoided, but pills reserved for true circular
 * elements to stay within the "Professional/B2B" register.
 */
@Immutable
data class DukaShapes(
    val small: RoundedCornerShape,   // 4px — buttons, chips, small components
    val medium: RoundedCornerShape,  // 6px
    val large: RoundedCornerShape,   // 8px — cards, modals, containers
    val extraLarge: RoundedCornerShape, // 12px — large sheets/hero cards
    val full: RoundedCornerShape,    // pill / circular
) {
    companion object {
        val Default = DukaShapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(6.dp),
            large = RoundedCornerShape(8.dp),
            extraLarge = RoundedCornerShape(12.dp),
            full = RoundedCornerShape(percent = 50),
        )
    }
}

/** Maps [DukaShapes] onto the Material 3 [Shapes] slots consumed by MaterialTheme. */
val DukaMaterialShapes = Shapes(
    extraSmall = DukaShapes.Default.small,
    small = DukaShapes.Default.small,
    medium = DukaShapes.Default.medium,
    large = DukaShapes.Default.large,
    extraLarge = DukaShapes.Default.extraLarge,
)