package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.R
import io.appkitchen.ante.core.designsystem.theme.AnteStateLayers
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

enum class ButtonStyle {
    Primary,
    Tonal,
    Text,
}

/**
 * The one button (spec §3.1). Three styles, one shape, one height; no error state exists.
 *
 * Built on Material's [Button] so pressed and focus layers come from the shared ripple rather than
 * being drawn here, and so disabled is announced natively. What is overridden is exactly what the
 * spec pins: colors per style from the theme, corner large, min height at the touch target, and
 * screenHorizontal padding.
 *
 * [loading] swaps the label for an 18dp indicator without changing the button's width - the label
 * is still measured, just not drawn - so a form's CTA does not jump while it records. Loading keeps
 * the enabled look but disables the click, and says so to accessibility.
 */
@Composable
fun AnteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val loadingState = stringResource(R.string.ante_state_loading)
    Button(
        onClick = onClick,
        // Loading blocks the click through `enabled` while its colors keep the enabled look; a
        // disabled button that is not loading gets the disabled colors as usual.
        enabled = enabled && !loading,
        colors = buttonColors(style, showAsEnabled = enabled && loading),
        // Tonal elevation level0, no shadow, in every state - the sheet has no shadow anywhere.
        elevation = null,
        shape = AnteTheme.shapes.large,
        contentPadding =
            PaddingValues(
                horizontal = AnteTheme.spacing.screenHorizontal,
                vertical = AnteTheme.spacing.sm,
            ),
        modifier =
            modifier
                .heightIn(min = AnteTheme.spacing.minTouchTarget)
                .then(
                    if (loading) Modifier.semantics { stateDescription = loadingState }
                    else Modifier
                ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                // Measured at full opacity so width is locked; only the paint changes.
                modifier = if (loading) Modifier.alpha(0f) else Modifier,
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(ICON_SIZE),
                    )
                    Spacer(Modifier.width(AnteTheme.spacing.relatedGap))
                }
                Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis, softWrap = false)
            }
            if (loading) {
                CircularProgressIndicator(
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(LOADING_INDICATOR_SIZE),
                )
            }
        }
    }
}

@Composable
private fun buttonColors(style: ButtonStyle, showAsEnabled: Boolean): ButtonColors {
    val scheme = AnteTheme.colorScheme
    val (container, content) =
        when (style) {
            ButtonStyle.Primary -> scheme.primary to scheme.onPrimary
            ButtonStyle.Tonal -> scheme.secondaryContainer to scheme.onSecondaryContainer
            ButtonStyle.Text -> Color.Transparent to scheme.primary
        }
    val disabledContainer =
        when (style) {
            ButtonStyle.Text -> Color.Transparent
            else -> scheme.onSurface.copy(alpha = AnteStateLayers.DISABLED_CONTAINER)
        }
    val disabledContent = scheme.onSurface.copy(alpha = AnteStateLayers.DISABLED_CONTENT)
    return ButtonColors(
        containerColor = container,
        contentColor = content,
        disabledContainerColor = if (showAsEnabled) container else disabledContainer,
        disabledContentColor = if (showAsEnabled) content else disabledContent,
    )
}

private val ICON_SIZE = 18.dp
private val LOADING_INDICATOR_SIZE = 18.dp
