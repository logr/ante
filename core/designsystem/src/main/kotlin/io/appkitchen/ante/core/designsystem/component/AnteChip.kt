package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

enum class ChipVariant {
    /** Single-select, as in WHO PAID. */
    Choice,
    /** A small button. */
    Action,
    /** A tag ("Voided"). Ignores [AnteChip]'s `selected` and `onClick`; no target, no role. */
    Static,
}

/**
 * The chip (spec §3.7): a choice in a single-select group, a small action, or a static tag.
 *
 * Choice and Action are Material [Surface]s with the selectable/clickable overloads, so pressed and
 * focus layers are the shared ripple on the variant's content color, and the touch target expands
 * to 48dp around a 32dp visual through Material's minimum-interactive-size rule rather than by
 * padding drawn here. A Choice or Action with no `onClick` keeps the 48dp layout so a row of chips
 * stays aligned when one is inert. Static is 20dp, plain text, and reserves no target at all.
 *
 * No icon slot and no disabled state exist. Labels are one line, ellipsized, at most 200dp wide - a
 * 30-character member name truncates.
 */
@Composable
fun AnteChip(
    label: String,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.Choice,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val scheme = AnteTheme.colorScheme
    when (variant) {
        ChipVariant.Choice ->
            InteractiveChip(
                label = label,
                container = if (selected) scheme.secondaryContainer else Color.Transparent,
                content = if (selected) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
                border = if (selected) null else BorderStroke(1.dp, scheme.outline),
                role = Role.RadioButton,
                selected = selected,
                onClick = onClick,
                modifier = modifier,
            )
        ChipVariant.Action ->
            InteractiveChip(
                label = label,
                container = Color.Transparent,
                content = scheme.primary,
                border = BorderStroke(1.dp, scheme.outline),
                role = Role.Button,
                selected = null,
                onClick = onClick,
                modifier = modifier,
            )
        ChipVariant.Static ->
            Surface(
                shape = AnteTheme.shapes.small,
                color = Color.Transparent,
                contentColor = scheme.onSurfaceVariant,
                border = BorderStroke(1.dp, scheme.outlineVariant),
                modifier = modifier.widthIn(max = MAX_WIDTH),
            ) {
                ChipLabel(label, AnteTheme.typography.labelSmall, STATIC_HEIGHT)
            }
    }
}

/**
 * Choice and Action share everything but colors and role. [selected] non-null selects the
 * selectable Surface overload (Choice); null the clickable one (Action).
 */
@Composable
private fun InteractiveChip(
    label: String,
    container: Color,
    content: Color,
    border: BorderStroke?,
    role: Role,
    selected: Boolean?,
    onClick: (() -> Unit)?,
    modifier: Modifier,
) {
    val shape = AnteTheme.shapes.small
    // The height goes on the label, inside the surface, not on the surface itself: Material's
    // clickable Surface puts minimumInteractiveComponentSize outermost, and a height constraint
    // outside it would cap the touch target at the visual height.
    val sized = modifier.widthIn(max = MAX_WIDTH)
    val body: @Composable () -> Unit = {
        ChipLabel(label, AnteTheme.typography.labelLarge, VISUAL_HEIGHT)
    }
    when {
        onClick == null ->
            Surface(
                shape = shape,
                color = container,
                contentColor = content,
                border = border,
                modifier = sized.minimumInteractiveComponentSize(),
                content = body,
            )
        selected != null ->
            Surface(
                selected = selected,
                onClick = onClick,
                shape = shape,
                color = container,
                contentColor = content,
                border = border,
                modifier = sized.semantics { this.role = role },
                content = body,
            )
        else ->
            Surface(
                onClick = onClick,
                shape = shape,
                color = container,
                contentColor = content,
                border = border,
                modifier = sized.semantics { this.role = role },
                content = body,
            )
    }
}

@Composable
private fun ChipLabel(label: String, style: TextStyle, height: Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(height).padding(horizontal = AnteTheme.spacing.md),
    ) {
        Text(
            text = label,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
        )
    }
}

private val VISUAL_HEIGHT = 32.dp
private val STATIC_HEIGHT = 20.dp
private val MAX_WIDTH = 200.dp
