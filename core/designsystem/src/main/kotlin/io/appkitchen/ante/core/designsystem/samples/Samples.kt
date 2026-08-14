package io.appkitchen.ante.core.designsystem.samples

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.theme.AnteMoneyColors
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

/**
 * Renderings of every token family, as ordinary composables.
 *
 * Public API on purpose. These are the single source of truth for what a token looks like: the
 * catalog renders them, and screenshot tests will capture the same functions. Two separate
 * renderings of the same token would be free to drift, which defeats having either.
 *
 * Scroll-free by design - the containing screen owns scrolling, so a capture of a sample is the
 * whole sample.
 */
@Composable
fun TokenSample(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AnteTheme.spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(AnteTheme.spacing.lg),
    ) {
        ColorRoleSample()
        MoneyToneSample()
        TypeRampSample()
        SpacingSample()
    }
}

@Composable
fun ColorRoleSample(modifier: Modifier = Modifier) {
    val scheme = AnteTheme.colorScheme
    SampleSection(title = "Color roles", modifier = modifier) {
        Swatch("primary", scheme.primary, scheme.onPrimary)
        Swatch("primaryContainer", scheme.primaryContainer, scheme.onPrimaryContainer)
        Swatch("secondary", scheme.secondary, scheme.onSecondary)
        Swatch("tertiary", scheme.tertiary, scheme.onTertiary)
        Swatch("error", scheme.error, scheme.onError)
        Swatch("surface", scheme.surface, scheme.onSurface)
        Swatch("surfaceVariant", scheme.surfaceVariant, scheme.onSurfaceVariant)
        Swatch("surfaceContainerHigh", scheme.surfaceContainerHigh, scheme.onSurface)
        Swatch("outline", scheme.outline, scheme.surface)
    }
}

@Composable
fun MoneyToneSample(modifier: Modifier = Modifier) {
    SampleSection(title = "Money tones", modifier = modifier) {
        MoneyTone.entries.forEach { tone ->
            val colors = AnteTheme.money.forTone(tone)
            Swatch(tone.name, colors.container, colors.onContainer)
        }

        // The prefix and weight are what carry owed-vs-owing when color cannot. Rendered here so
        // a golden covers them: losing the cue is a real accessibility regression, and it is
        // invisible in a swatch.
        Text(
            text = "Tone cues",
            style = AnteTheme.typography.labelMedium,
            modifier = Modifier.padding(top = AnteTheme.spacing.sm),
        )
        MoneyTone.entries.forEach { tone ->
            val cue = AnteMoneyColors.cueFor(tone)
            Text(
                text = "${cue.prefix}42.00  ${tone.name}",
                color = AnteTheme.money.forTone(tone).content,
                style = AnteTheme.textStyles.moneyMedium.copy(fontWeight = cue.weight),
            )
        }

        Text(
            text = "Tabular alignment",
            style = AnteTheme.typography.labelMedium,
            modifier = Modifier.padding(top = AnteTheme.spacing.sm),
        )
        // Deliberately awkward digits: if tabular figures are not actually active in the
        // rendered font, these three lines visibly fail to line up.
        listOf("1,111.11", "8,888.88", "-99.00").forEach { amount ->
            Text(
                text = amount,
                style = AnteTheme.textStyles.moneyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.width(140.dp),
            )
        }
    }
}

@Composable
fun TypeRampSample(modifier: Modifier = Modifier) {
    val typography = AnteTheme.typography
    SampleSection(title = "Type ramp", modifier = modifier) {
        Text("headlineMedium", style = typography.headlineMedium)
        Text("titleLarge", style = typography.titleLarge)
        Text("titleMedium", style = typography.titleMedium)
        Text("bodyLarge", style = typography.bodyLarge)
        Text("bodyMedium", style = typography.bodyMedium)
        Text("labelMedium", style = typography.labelMedium)
        Text("moneyLarge", style = AnteTheme.textStyles.moneyLarge)
        Text("moneySmall", style = AnteTheme.textStyles.moneySmall)
    }
}

@Composable
fun SpacingSample(modifier: Modifier = Modifier) {
    val spacing = AnteTheme.spacing
    SampleSection(title = "Spacing scale", modifier = modifier) {
        listOf(
                "xxs" to spacing.xxs,
                "xs" to spacing.xs,
                "sm" to spacing.sm,
                "md" to spacing.md,
                "lg" to spacing.lg,
                "xl" to spacing.xl,
                "xxl" to spacing.xxl,
            )
            .forEach { (name, size) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = AnteTheme.typography.labelMedium,
                        modifier = Modifier.width(48.dp),
                    )
                    Surface(
                        color = AnteTheme.colorScheme.primary,
                        modifier = Modifier.width(size).height(16.dp),
                        content = {},
                    )
                    Text(
                        text = " ${size.value.toInt()}dp",
                        style = AnteTheme.typography.labelSmall,
                    )
                }
            }
    }
}

@Composable
private fun SampleSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnteTheme.spacing.relatedGap),
    ) {
        Text(text = title, style = AnteTheme.typography.titleMedium)
        HorizontalDivider()
        content()
    }
}

@Composable
private fun Swatch(name: String, color: Color, onColor: Color) {
    // Outlined, so a swatch whose color matches the surface behind it (surface itself, and
    // several of the container roles) is still visibly a swatch.
    Surface(
        color = color,
        shape = AnteTheme.shapes.small,
        border = BorderStroke(1.dp, AnteTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = name,
            color = onColor,
            style = AnteTheme.typography.labelLarge,
            modifier = Modifier.padding(AnteTheme.spacing.sm),
        )
    }
}
