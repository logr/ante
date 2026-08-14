package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tabular (monospaced) figures, so a column of amounts lines up on the decimal point.
 *
 * Roboto's default figures are already equal-width; the feature is set anyway so the guarantee is
 * explicit rather than a property of the current font choice.
 */
private const val TABULAR_FIGURES = "tnum"

/**
 * Money type styles, from the design project's `tokens.json` (v2).
 *
 * Kept out of the Material [Typography] slots on purpose - see the note on [AnteTypography].
 *
 * These carry size only. Weight is a function of the tone, not the size, so a money component takes
 * the style for the size it needs and overrides the weight from [AnteMoneyColors.cueFor]. The
 * weight here is the common case, not an instruction.
 */
@Immutable
data class AnteTextStyles(
    val moneyLarge: TextStyle,
    val moneyMedium: TextStyle,
    val moneySmall: TextStyle,
) {
    companion object {
        val Default =
            AnteTextStyles(
                moneyLarge =
                    TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = TABULAR_FIGURES,
                    ),
                moneyMedium =
                    TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = TABULAR_FIGURES,
                    ),
                moneySmall =
                    TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = TABULAR_FIGURES,
                    ),
            )
    }
}

val LocalAnteTextStyles = staticCompositionLocalOf { AnteTextStyles.Default }
