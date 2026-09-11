package io.appkitchen.ante.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

/** How a share is expressed: a fixed amount or a percentage. The equal split has no share input. */
enum class SplitMode {
    Exact,
    Percent,
}

/**
 * A share, in the units the model stores it in. Minor units and basis points make the no-float rule
 * structural: there is no `Double` anywhere in the signature to be tempted by.
 */
sealed interface ShareValue {
    data class ExactMinor(val minor: Long) : ShareValue

    data class Percent(val basisPoints: Int) : ShareValue
}

/**
 * The plain text field (spec §3.6): a title, a group name, a member name.
 *
 * Label sits above the field and is always visible - no floating-label trick to lose. Single line;
 * overflow scrolls, user input is never ellipsized. No `enabled` exists: fields never disable, only
 * CTAs do. [isError] switches label, border and [supportingText] to the error color and announces
 * the supporting text as the error.
 *
 * [interactionSource] is the usual Material hook so a screen can observe focus (Add expense focuses
 * AMOUNT on open); the catalog uses it to show the focused state without owning the keyboard.
 */
@Composable
fun AnteTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    AnteBaseInput(
        text = value,
        onTextChange = onValueChange,
        label = label,
        placeholder = placeholder,
        isError = isError,
        supportingText = supportingText,
        height = TEXT_HEIGHT,
        textStyle = AnteTheme.typography.bodyLarge,
        keyboardType = KeyboardType.Text,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

/**
 * The amount field. Value in moneyLarge with a fixed "$" prefix; numeric keypad. Entry is
 * calculator-style over the minor-unit integer - typing shifts digits in from the right, backspace
 * shifts them out - so there is no parsing of a decimal string and no float anywhere. The caller
 * holds `amountMinor`; the field derives what it shows from it.
 */
@Composable
fun AnteCurrencyInput(
    amountMinor: Long,
    onAmountChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    AnteBaseInput(
        text = formatMinor(amountMinor),
        onTextChange = { onAmountChange(minorFromDigits(it)) },
        label = label,
        placeholder = null,
        isError = isError,
        supportingText = supportingText,
        height = CURRENCY_HEIGHT,
        textStyle = AnteTheme.textStyles.moneyLarge,
        keyboardType = KeyboardType.Number,
        prefix = CURRENCY_SYMBOL,
        // A zero amount reads as the placeholder it effectively is.
        placeholderLike = amountMinor == 0L,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

/**
 * A per-row share on Add expense. Right-aligned, "$" prefix in [SplitMode.Exact], "%" suffix in
 * [SplitMode.Percent] - integer percent in the UI over basis-point storage. 48dp, so the touch
 * target is met by height exactly. No label: the member's name beside it is the label.
 *
 * [isError] is for the share-is-zero case only. Over- and under-sum are properties of the set, not
 * of any row, and surface in the running tally and the banner - rows render identically either way.
 */
@Composable
fun AnteShareInput(
    value: ShareValue,
    onValueChange: (ShareValue) -> Unit,
    mode: SplitMode,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val (text, prefix, suffix, placeholderLike) =
        when (value) {
            is ShareValue.ExactMinor ->
                ShareDisplay(formatMinor(value.minor), CURRENCY_SYMBOL, null, value.minor == 0L)
            is ShareValue.Percent ->
                ShareDisplay(
                    formatBasisPoints(value.basisPoints),
                    null,
                    PERCENT_SYMBOL,
                    value.basisPoints == 0,
                )
        }
    AnteBaseInput(
        text = text,
        onTextChange = { typed ->
            onValueChange(
                when (mode) {
                    SplitMode.Exact -> ShareValue.ExactMinor(minorFromDigits(typed))
                    SplitMode.Percent -> ShareValue.Percent(basisPointsFromDigits(typed))
                }
            )
        },
        label = null,
        placeholder = null,
        isError = isError,
        supportingText = null,
        height = SHARE_HEIGHT,
        textStyle = AnteTheme.textStyles.moneyMedium,
        keyboardType = KeyboardType.Number,
        prefix = prefix,
        suffix = suffix,
        textAlign = TextAlign.End,
        placeholderLike = placeholderLike,
        interactionSource = interactionSource,
        modifier = modifier,
    )
}

private data class ShareDisplay(
    val text: String,
    val prefix: String?,
    val suffix: String?,
    val placeholderLike: Boolean,
)

/**
 * All the chrome, once. Label above, bordered box at a fixed height with screenHorizontal padding,
 * corner extra-small, supporting text below; the three public inputs differ only in what they hand
 * in here, so their appearance agrees by construction.
 *
 * Numeric inputs pin the cursor to the end so entry stays calculator-style; a caret dropped in the
 * middle of a formatted amount has no meaning under digit-shift entry.
 */
@Composable
private fun AnteBaseInput(
    text: String,
    onTextChange: (String) -> Unit,
    label: String?,
    placeholder: String?,
    isError: Boolean,
    supportingText: String?,
    height: Dp,
    textStyle: TextStyle,
    keyboardType: KeyboardType,
    interactionSource: MutableInteractionSource?,
    modifier: Modifier,
    prefix: String? = null,
    suffix: String? = null,
    textAlign: TextAlign = TextAlign.Start,
    placeholderLike: Boolean = false,
) {
    val scheme = AnteTheme.colorScheme
    val spacing = AnteTheme.spacing
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()

    val borderColor =
        when {
            isError -> scheme.error
            focused -> scheme.primary
            else -> scheme.outline
        }
    val borderWidth = if (isError || focused) FOCUSED_BORDER else RESTING_BORDER
    val labelColor =
        when {
            isError -> scheme.error
            focused -> scheme.primary
            else -> scheme.onSurfaceVariant
        }
    val valueColor = if (placeholderLike) scheme.onSurfaceVariant else scheme.onSurface
    val numeric = keyboardType == KeyboardType.Number

    val errorModifier =
        if (isError && supportingText != null) {
            Modifier.semantics { error(supportingText) }
        } else {
            Modifier
        }
    Column(modifier = modifier.then(errorModifier)) {
        if (label != null) {
            Text(text = label, color = labelColor, style = AnteTheme.typography.bodySmall)
            Spacer(Modifier.height(spacing.inlineGap))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                // A minimum, not a fixed height: at 1x it is exact; at 2x the field grows rather
                // than clip the value.
                Modifier.fillMaxWidth()
                    .heightIn(min = height)
                    .border(borderWidth, borderColor, AnteTheme.shapes.extraSmall)
                    .padding(horizontal = spacing.screenHorizontal),
        ) {
            if (prefix != null) {
                Text(text = prefix, color = valueColor, style = textStyle)
                Spacer(Modifier.width(spacing.inlineGap))
            }
            val fieldStyle = textStyle.copy(color = valueColor, textAlign = textAlign)
            if (numeric) {
                // TextFieldValue rather than String so the selection can be held at the end.
                BasicTextField(
                    value = TextFieldValue(text, selection = TextRange(text.length)),
                    onValueChange = { onTextChange(it.text) },
                    textStyle = fieldStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    cursorBrush = SolidColor(scheme.primary),
                    interactionSource = source,
                    modifier = Modifier.weight(1f),
                )
            } else {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = fieldStyle,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    cursorBrush = SolidColor(scheme.primary),
                    interactionSource = source,
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (text.isEmpty() && placeholder != null) {
                                Text(
                                    text = placeholder,
                                    color = scheme.onSurfaceVariant,
                                    style = textStyle,
                                    maxLines = 1,
                                )
                            }
                            inner()
                        }
                    },
                )
            }
            if (suffix != null) {
                Spacer(Modifier.width(spacing.inlineGap))
                Text(text = suffix, color = valueColor, style = textStyle)
            }
        }
        if (supportingText != null) {
            Spacer(Modifier.height(spacing.inlineGap))
            Text(
                text = supportingText,
                color = if (isError) scheme.error else scheme.onSurfaceVariant,
                style = AnteTheme.typography.bodySmall,
                modifier =
                    if (isError) Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    else Modifier,
            )
        }
    }
}

// Digit-shift entry helpers. Digits only, everything else in the typed string is dropped, so a
// backspace over a separator or a pasted "$1,204.50" both resolve to the digits they contain. At
// the cap, further digits are ignored rather than shifting the leading ones out.

internal fun minorFromDigits(typed: String): Long {
    val digits = typed.filter { it.isDigit() }.trimStart('0').take(MAX_DIGITS)
    return if (digits.isEmpty()) 0L else digits.toLong()
}

internal fun basisPointsFromDigits(typed: String): Int {
    val digits = typed.filter { it.isDigit() }.trimStart('0').take(MAX_PERCENT_DIGITS)
    return if (digits.isEmpty()) 0 else digits.toInt() * BASIS_POINTS_PER_PERCENT
}

/** Minor units to "1,204,517.30": grouped integer part, always exactly two decimals. */
internal fun formatMinor(minor: Long): String {
    val padded = minor.toString().padStart(3, '0')
    val integer = padded.dropLast(2)
    val fraction = padded.takeLast(2)
    val grouped = integer.reversed().chunked(3).joinToString(GROUPING_SEPARATOR).reversed()
    return "$grouped.$fraction"
}

/** Basis points to an integer percent when whole, otherwise the fraction it actually holds. */
internal fun formatBasisPoints(basisPoints: Int): String {
    val whole = basisPoints / BASIS_POINTS_PER_PERCENT
    val remainder = basisPoints % BASIS_POINTS_PER_PERCENT
    return if (remainder == 0) whole.toString()
    else "$whole.${remainder.toString().padStart(2, '0')}"
}

private const val CURRENCY_SYMBOL = "$"
private const val PERCENT_SYMBOL = "%"
private const val GROUPING_SEPARATOR = ","
private const val BASIS_POINTS_PER_PERCENT = 100
/** Sign + 7 digits + separators is the column reserve (spec §2); the field caps entry there. */
private const val MAX_DIGITS = 9
private const val MAX_PERCENT_DIGITS = 3
private val TEXT_HEIGHT = 56.dp
private val CURRENCY_HEIGHT = 64.dp
private val SHARE_HEIGHT = 48.dp
private val RESTING_BORDER = 1.dp
private val FOCUSED_BORDER = 2.dp
