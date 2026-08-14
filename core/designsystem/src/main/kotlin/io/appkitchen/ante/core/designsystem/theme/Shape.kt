package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii from the design project's `tokens.json` (v2).
 *
 * These happen to match the Material 3 baseline scale, but they are stated rather than left to
 * `Shapes()` for the same reason the color scheme lists every slot: the token sheet is the source
 * of truth, and an upstream default that shifts should not silently move the brand.
 */
internal val AnteShapes: Shapes =
    Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )
