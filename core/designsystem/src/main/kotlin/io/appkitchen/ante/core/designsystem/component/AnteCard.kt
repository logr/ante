package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.theme.AnteElevation
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

enum class CardVariant {
    Filled,
    Elevated,
    Outlined,
}

/**
 * A container (spec §3.4); its one current use is the settle-up plan on Balances.
 *
 * Free content slot on a Material [Surface]. With [onClick] the card is one merged target with the
 * shared ripple and Material's 48dp minimum; without it the card is semantically transparent and
 * its children own their semantics. The two modes are not to be mixed - a clickable card must not
 * contain interactive children.
 *
 * Elevated is tonal level1 with no shadow: the sheet has no shadow anywhere.
 */
@Composable
fun AnteCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Filled,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scheme = AnteTheme.colorScheme
    val color =
        when (variant) {
            CardVariant.Filled -> scheme.surfaceContainerHighest
            CardVariant.Elevated -> scheme.surfaceContainerLow
            CardVariant.Outlined -> scheme.surface
        }
    val tonalElevation =
        when (variant) {
            CardVariant.Elevated -> AnteElevation.level1
            else -> AnteElevation.level0
        }
    val border =
        when (variant) {
            CardVariant.Outlined -> BorderStroke(1.dp, scheme.outlineVariant)
            else -> null
        }
    val body: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(AnteTheme.spacing.screenHorizontal), content = content)
    }
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = AnteTheme.shapes.medium,
            color = color,
            contentColor = scheme.onSurface,
            tonalElevation = tonalElevation,
            border = border,
            content = body,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = AnteTheme.shapes.medium,
            color = color,
            contentColor = scheme.onSurface,
            tonalElevation = tonalElevation,
            border = border,
            content = body,
        )
    }
}
