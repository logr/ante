package io.appkitchen.ante.core.designsystem.samples

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.component.AnteCurrencyInput
import io.appkitchen.ante.core.designsystem.component.AnteShareInput
import io.appkitchen.ante.core.designsystem.component.AnteTextInput
import io.appkitchen.ante.core.designsystem.component.ShareValue
import io.appkitchen.ante.core.designsystem.component.SplitMode

/**
 * Spec §3.6 screenshots: Text/Currency/Share x enabled/focused/error, light + dark; Currency at 2x
 * font scale. 20 frames.
 *
 * Values are hoisted into the frame so the catalog's fields actually accept typing; a golden
 * captures the initial value. Labels and placeholders are the wireframes' ("TITLE", "AMOUNT", "What
 * was it for?"); the two supporting-text strings are sample copy, since the spec fixes no copy for
 * these two error cases.
 */
val TextInputSample: ComponentSample =
    ComponentSample(
        id = "text_input",
        title = "AnteTextInput",
        frames =
            listOf(
                SampleFrame("text_enabled") { TextFrame(initial = "") },
                SampleFrame("text_focused") { TextFrame(initial = "Dinner", focused = true) },
                SampleFrame("text_error") {
                    TextFrame(
                        initial = "",
                        label = "GROUP NAME",
                        placeholder = "Add a name…",
                        error = "A group needs a name.",
                    )
                },
                SampleFrame("currency_enabled") { CurrencyFrame(initial = 0) },
                SampleFrame("currency_focused") { CurrencyFrame(initial = 4200, focused = true) },
                SampleFrame("currency_error") {
                    CurrencyFrame(initial = 2000, error = "Couldn't save this amount. Try again.")
                },
                SampleFrame("share_enabled") {
                    ShareFrame(initial = ShareValue.ExactMinor(1550), mode = SplitMode.Exact)
                },
                SampleFrame("share_focused") {
                    ShareFrame(
                        initial = ShareValue.Percent(2500),
                        mode = SplitMode.Percent,
                        focused = true,
                    )
                },
                SampleFrame("share_error") {
                    ShareFrame(
                        initial = ShareValue.ExactMinor(0),
                        mode = SplitMode.Exact,
                        isError = true,
                    )
                },
                SampleFrame("currency_font_2x", fontScale = 2f) { CurrencyFrame(initial = 4200) },
            ),
    )

@Composable
private fun TextFrame(
    initial: String,
    label: String = "TITLE",
    placeholder: String = "What was it for?",
    focused: Boolean = false,
    error: String? = null,
) {
    var value by remember { mutableStateOf(initial) }
    AnteTextInput(
        value = value,
        onValueChange = { value = it },
        label = label,
        placeholder = placeholder,
        isError = error != null,
        supportingText = error,
        interactionSource = if (focused) focusedInteractionSource() else null,
    )
}

@Composable
private fun CurrencyFrame(initial: Long, focused: Boolean = false, error: String? = null) {
    var amount by remember { mutableLongStateOf(initial) }
    AnteCurrencyInput(
        amountMinor = amount,
        onAmountChange = { amount = it },
        label = "AMOUNT",
        isError = error != null,
        supportingText = error,
        interactionSource = if (focused) focusedInteractionSource() else null,
    )
}

@Composable
private fun ShareFrame(
    initial: ShareValue,
    mode: SplitMode,
    focused: Boolean = false,
    isError: Boolean = false,
) {
    var value by remember { mutableStateOf(initial) }
    AnteShareInput(
        value = value,
        onValueChange = { value = it },
        mode = mode,
        isError = isError,
        interactionSource = if (focused) focusedInteractionSource() else null,
        // Share inputs sit beside a member name on Add expense; the frame gives them that width.
        modifier = Modifier.width(SHARE_INPUT_WIDTH),
    )
}

/**
 * The focused state without owning focus: a focus interaction pushed into the field's interaction
 * source, which is what the field's focus styling reads. Real focus would fight between the three
 * focused frames on one catalog page and would summon the keyboard; this shows all three at once
 * and renders identically in a golden.
 *
 * Emitted a frame late on purpose: the source has no replay, and this effect is registered before
 * the field's collector (it is composed in the field's argument list), so an interaction emitted
 * straight away would be dropped. After one frame the field is collecting.
 */
@Composable
private fun focusedInteractionSource(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) {
        withFrameNanos {}
        source.emit(FocusInteraction.Focus())
    }
    return source
}

private val SHARE_INPUT_WIDTH = 140.dp
