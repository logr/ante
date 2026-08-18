package io.appkitchen.ante.core.designsystem.samples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.appkitchen.ante.core.designsystem.component.AnteCard
import io.appkitchen.ante.core.designsystem.component.AnteMoneyText
import io.appkitchen.ante.core.designsystem.component.CardVariant
import io.appkitchen.ante.core.designsystem.component.MoneyStyle
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

/** Spec §3.4 screenshots: Filled/Elevated/Outlined, light + dark. 6 frames. */
val CardSample: ComponentSample =
    ComponentSample(
        id = "card",
        title = "AnteCard",
        frames =
            listOf(
                SampleFrame("filled") { PlanCard(CardVariant.Filled) },
                SampleFrame("elevated") { PlanCard(CardVariant.Elevated) },
                SampleFrame("outlined") { PlanCard(CardVariant.Outlined) },
            ),
    )

/**
 * The card's one real use, the settle-up plan, so the frames show it holding the content it was
 * added for. Copy keeps to the spec's guarantee wording - never "fewest", "minimal" or "optimal".
 */
@Composable
private fun PlanCard(variant: CardVariant) {
    AnteCard(variant = variant, modifier = Modifier.fillMaxWidth()) {
        Text("Settle-up plan", style = AnteTheme.typography.titleMedium)
        Text(
            "2 transfers, worked out the same way every time.",
            style = AnteTheme.typography.bodyMedium,
            color = AnteTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(AnteTheme.spacing.relatedGap))
        PlanLine("Sam pays Alex", "$20.00")
        PlanLine("Maya pays Alex", "$18.25")
    }
}

@Composable
private fun PlanLine(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = AnteTheme.typography.bodyLarge)
        AnteMoneyText(amount, tone = MoneyTone.Neutral, style = MoneyStyle.Small)
    }
}
