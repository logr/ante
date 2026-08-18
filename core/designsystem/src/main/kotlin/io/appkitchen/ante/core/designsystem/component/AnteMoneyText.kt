package io.appkitchen.ante.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import io.appkitchen.ante.core.designsystem.R
import io.appkitchen.ante.core.designsystem.theme.AnteMoneyColors
import io.appkitchen.ante.core.designsystem.theme.AnteStateLayers
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

/** Which money type style an amount takes: net position, row amount, or share line. */
enum class MoneyStyle {
    Large,
    Medium,
    Small,
}

/**
 * An amount, colored and weighted for its [tone].
 *
 * Does no formatting. [text] is the formatter's output, prefix included: the design system never
 * sees a `Money`, and the sign, symbol and grouping rules are one formatter's job (`core:ui`), not
 * something to be re-derived per component. What this owns is the rendering contract from the spec:
 * tone color, tone weight, tabular figures, and exactly one line that never truncates, wraps or
 * shrinks - the parent reserves the column width.
 *
 * Display only. Never a tap target, so no interaction states exist. Callers phrase the semantics
 * (`contentDescription`, "you are owed forty-two dollars fifty"); [voided] adds a state.
 */
@Composable
fun AnteMoneyText(
    text: String,
    modifier: Modifier = Modifier,
    tone: MoneyTone = MoneyTone.Neutral,
    style: MoneyStyle = MoneyStyle.Medium,
    voided: Boolean = false,
) {
    val cue = AnteMoneyColors.cueFor(tone)
    val color =
        if (voided) {
            AnteTheme.colorScheme.onSurface.copy(alpha = AnteStateLayers.DISABLED_CONTENT)
        } else {
            AnteTheme.money.forTone(tone).content
        }
    val voidedState = stringResource(R.string.ante_state_voided)
    Text(
        text = text,
        color = color,
        style = moneyTextStyle(style).copy(fontWeight = cue.weight),
        textDecoration = if (voided) TextDecoration.LineThrough else null,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        modifier = if (voided) modifier.semantics { stateDescription = voidedState } else modifier,
    )
}

@Composable
internal fun moneyTextStyle(style: MoneyStyle): TextStyle =
    when (style) {
        MoneyStyle.Large -> AnteTheme.textStyles.moneyLarge
        MoneyStyle.Medium -> AnteTheme.textStyles.moneyMedium
        MoneyStyle.Small -> AnteTheme.textStyles.moneySmall
    }
