package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The dp spacing scale, from the design project's `tokens.json` (v2). Material 3 has no slot for
 * this, so it rides its own CompositionLocal.
 *
 * Prefer the semantic aliases over the raw scale. Without them every component picks a step by
 * feel, and the same conceptual gap ends up [sm] in one component and [md] in the next.
 */
@Immutable
data class AnteSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val screenHorizontal: Dp = 16.dp,
    val listRowVertical: Dp = 12.dp,
    val relatedGap: Dp = 8.dp,
    val sectionGap: Dp = 24.dp,
    val inlineGap: Dp = 4.dp,
    val actionGap: Dp = 8.dp,
    /** Floor for anything tappable. Material's own minimum, restated so components can cite it. */
    val minTouchTarget: Dp = 48.dp,
) {
    companion object {
        val Default = AnteSpacing()
    }
}

/**
 * Static rather than dynamic: a spacing change should recompose the whole subtree rather than being
 * tracked per read. This is what Material 3 does for its own theme locals.
 */
val LocalAnteSpacing = staticCompositionLocalOf { AnteSpacing.Default }
