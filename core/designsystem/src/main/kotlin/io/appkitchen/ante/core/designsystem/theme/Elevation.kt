package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.ui.unit.dp

/**
 * Tonal elevation levels from the design project's `tokens.json` (v2). The token sheet defines no
 * custom shadow specs anywhere - elevation is expressed as a level and Material derives the surface
 * tint from it.
 *
 * A plain object rather than a CompositionLocal: these do not vary by theme, and nothing consumes
 * them yet. They become theme state only if a density or contrast axis ever needs them to.
 */
object AnteElevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
}
