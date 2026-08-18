package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteChip
import io.appkitchen.ante.core.designsystem.component.ChipVariant

/**
 * Spec §3.7 screenshots: Choice off/on, Action, Static, light + dark; one Choice at 2x font scale.
 * 9 frames.
 *
 * The Choice-off label is the spec's 30-character member name, so the 200dp cap and its ellipsis
 * are in a golden.
 */
val ChipSample: ComponentSample =
    ComponentSample(
        id = "chip",
        title = "AnteChip",
        frames =
            listOf(
                SampleFrame("choice_off") {
                    AnteChip("Alexandria Montgomery-Whitfield", onClick = {})
                },
                SampleFrame("choice_on") { AnteChip("Sam", selected = true, onClick = {}) },
                SampleFrame("action") {
                    AnteChip("Settle up", variant = ChipVariant.Action, onClick = {})
                },
                SampleFrame("static") { AnteChip("Voided", variant = ChipVariant.Static) },
                SampleFrame("choice_font_2x", themes = FrameThemes.LightOnly, fontScale = 2f) {
                    AnteChip("Maya", selected = true, onClick = {})
                },
            ),
    )
