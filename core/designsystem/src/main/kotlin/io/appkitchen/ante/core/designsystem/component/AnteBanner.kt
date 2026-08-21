package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.icons.AnteIcons
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

enum class BannerKind {
    Info,
    Error,
}

/**
 * An inline notice (spec §3.8): the one component whose text is unlimited - it wraps fully and
 * never truncates.
 *
 * No dismiss, no action, no interaction states: it appears and disappears only with the condition
 * it reports, and the caller animates that (short4, standardDecelerate) since the banner is
 * stateless. Info is a polite live region, Error an assertive one. The margin the spec gives it -
 * screenHorizontal by relatedGap - is part of the component, so it sits under an app bar or above
 * the split tally with no positioning by the caller.
 */
@Composable
fun AnteBanner(text: String, modifier: Modifier = Modifier, kind: BannerKind = BannerKind.Info) {
    val scheme = AnteTheme.colorScheme
    val spacing = AnteTheme.spacing
    val (container, content, iconTint, icon, liveRegionMode) =
        when (kind) {
            BannerKind.Info ->
                BannerStyle(
                    container = scheme.surfaceContainerHigh,
                    content = scheme.onSurface,
                    iconTint = scheme.onSurfaceVariant,
                    icon = AnteIcons.Info,
                    liveRegion = LiveRegionMode.Polite,
                )
            BannerKind.Error ->
                BannerStyle(
                    container = scheme.errorContainer,
                    content = scheme.onErrorContainer,
                    iconTint = scheme.onErrorContainer,
                    icon = AnteIcons.ErrorOutline,
                    liveRegion = LiveRegionMode.Assertive,
                )
        }
    Surface(
        color = container,
        contentColor = content,
        shape = AnteTheme.shapes.small,
        modifier =
            modifier
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.relatedGap)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) { liveRegion = liveRegionMode },
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier =
                Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.md),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                // Top-aligned: at 20dp it sits on the first line of body text and stays there
                // when the text wraps.
                modifier = Modifier.size(ICON_SIZE),
            )
            Spacer(Modifier.width(spacing.relatedGap))
            Text(text = text, style = AnteTheme.typography.bodyMedium)
        }
    }
}

private data class BannerStyle(
    val container: Color,
    val content: Color,
    val iconTint: Color,
    val icon: ImageVector,
    val liveRegion: LiveRegionMode,
)

private val ICON_SIZE = 20.dp
