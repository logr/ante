package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * How a balance reads to the member looking at it.
 *
 * Deliberately not a sign: -$5.00 means "owed to you" or "you owe" depending on whose row it is,
 * and the component must not have to work that out. The caller resolves perspective; this names the
 * result.
 */
enum class MoneyTone {
    /** Someone owes the viewer. */
    Owed,
    /** The viewer owes someone. */
    Owing,
    /** Nothing outstanding either way. */
    Settled,
    /** Recorded locally, not yet synced. */
    Pending,
    /** An amount with no balance meaning - an expense total, an input value. */
    Neutral,
}

/**
 * The cue that carries a tone's meaning when color does not.
 *
 * Color is never the sole carrier, so every tone also gets a prefix and a weight. Note that
 * [weight] varies by tone while [AnteTextStyles] varies by size: a money component picks a size
 * style and then overrides its weight from here. That is why weight is not baked into the text
 * styles.
 *
 * [prefix] for Owing is U+2212 MINUS SIGN, not a hyphen - it aligns with the digits and reads as
 * arithmetic rather than punctuation.
 */
@Immutable data class MoneyToneCue(val prefix: String, val weight: FontWeight)

@Immutable
data class MoneyToneColors(val content: Color, val container: Color, val onContainer: Color)

/**
 * Semantic money colors, transcribed from the design project's `tokens.json` (v2). Material 3 has
 * no slot for these and no generator produces them, so they are their own token family.
 *
 * The palette is green/rust rather than the usual red/green, which already helps for the commonest
 * color vision deficiencies - and [cueFor] carries the meaning again without color, so the
 * distinction survives greyscale entirely.
 */
@Immutable
data class AnteMoneyColors(
    val owed: MoneyToneColors,
    val owing: MoneyToneColors,
    val settled: MoneyToneColors,
    val pending: MoneyToneColors,
    val neutral: MoneyToneColors,
) {
    fun forTone(tone: MoneyTone): MoneyToneColors =
        when (tone) {
            MoneyTone.Owed -> owed
            MoneyTone.Owing -> owing
            MoneyTone.Settled -> settled
            MoneyTone.Pending -> pending
            MoneyTone.Neutral -> neutral
        }

    companion object {
        /** Settled renders "Settled up" alongside a zero amount, so it needs no prefix. */
        fun cueFor(tone: MoneyTone): MoneyToneCue =
            when (tone) {
                MoneyTone.Owed -> MoneyToneCue(prefix = "+", weight = FontWeight.Medium)
                MoneyTone.Owing -> MoneyToneCue(prefix = "−", weight = FontWeight.Normal)
                MoneyTone.Settled -> MoneyToneCue(prefix = "", weight = FontWeight.Normal)
                MoneyTone.Pending -> MoneyToneCue(prefix = "", weight = FontWeight.Normal)
                MoneyTone.Neutral -> MoneyToneCue(prefix = "", weight = FontWeight.Medium)
            }
    }
}

// Measured contrast from tokens.json: every content-on-surface pair is at least 6.1:1 and every
// onContainer-on-container pair at least 7.2:1, against a 4.5:1 target.
internal val AnteLightMoneyColors =
    AnteMoneyColors(
        owed =
            MoneyToneColors(
                content = Color(0xFF2D6A43),
                container = Color(0xFFB0F1C3),
                onContainer = Color(0xFF00210B),
            ),
        owing =
            MoneyToneColors(
                content = Color(0xFF9C4420),
                container = Color(0xFFFFDBCD),
                onContainer = Color(0xFF321300),
            ),
        settled =
            MoneyToneColors(
                content = Color(0xFF5C5E69),
                container = Color(0xFFE0E2EF),
                onContainer = Color(0xFF191B24),
            ),
        pending =
            MoneyToneColors(
                content = Color(0xFF755B00),
                container = Color(0xFFFEDF9D),
                onContainer = Color(0xFF241A00),
            ),
        // Not in tokens.json: Neutral is the "no balance meaning" case, so it takes onSurface and
        // the highest surface container rather than a semantic color of its own.
        neutral =
            MoneyToneColors(
                content = Color(0xFF1C1B1E),
                container = Color(0xFFE3E2E6),
                onContainer = Color(0xFF1C1B1E),
            ),
    )

internal val AnteDarkMoneyColors =
    AnteMoneyColors(
        owed =
            MoneyToneColors(
                content = Color(0xFF95D5A7),
                container = Color(0xFF11512D),
                onContainer = Color(0xFFB0F1C3),
            ),
        owing =
            MoneyToneColors(
                content = Color(0xFFFFB598),
                container = Color(0xFF7E2B09),
                onContainer = Color(0xFFFFDBCD),
            ),
        settled =
            MoneyToneColors(
                content = Color(0xFFC4C6D3),
                container = Color(0xFF444651),
                onContainer = Color(0xFFE0E2EF),
            ),
        pending =
            MoneyToneColors(
                content = Color(0xFFE9C260),
                container = Color(0xFF584400),
                onContainer = Color(0xFFFEDF9D),
            ),
        neutral =
            MoneyToneColors(
                content = Color(0xFFE3E2E6),
                container = Color(0xFF353438),
                onContainer = Color(0xFFE3E2E6),
            ),
    )

/**
 * No default: a component reached outside [AnteTheme] should fail at first composition rather than
 * render plausible-but-wrong colors that a screenshot would then bless as correct.
 */
val LocalAnteMoneyColors =
    staticCompositionLocalOf<AnteMoneyColors> {
        error("AnteMoneyColors not provided - wrap the call site in AnteTheme")
    }
