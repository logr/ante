package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteButton
import io.appkitchen.ante.core.designsystem.component.ButtonStyle
import io.appkitchen.ante.core.designsystem.icons.AnteIcons

/**
 * Spec §3.1 screenshots: Primary/Tonal/Text x enabled/disabled, light + dark; Primary loading;
 * Primary at 2x font scale. 14 frames.
 *
 * The Tonal frames carry a leading icon so the icon slot and its gap are in a golden; the matrix
 * says which variant and state each frame shows, not what label it carries.
 */
val ButtonSample: ComponentSample =
    ComponentSample(
        id = "button",
        title = "AnteButton",
        frames =
            listOf(
                SampleFrame("primary_enabled") { AnteButton("Record expense", onClick = {}) },
                SampleFrame("primary_disabled") {
                    AnteButton("Record expense", onClick = {}, enabled = false)
                },
                SampleFrame("tonal_enabled") {
                    AnteButton(
                        "Add member",
                        onClick = {},
                        style = ButtonStyle.Tonal,
                        leadingIcon = AnteIcons.GroupAdd,
                    )
                },
                SampleFrame("tonal_disabled") {
                    AnteButton(
                        "Add member",
                        onClick = {},
                        style = ButtonStyle.Tonal,
                        enabled = false,
                        leadingIcon = AnteIcons.GroupAdd,
                    )
                },
                SampleFrame("text_enabled") {
                    AnteButton("Void this expense", onClick = {}, style = ButtonStyle.Text)
                },
                SampleFrame("text_disabled") {
                    AnteButton(
                        "Void this expense",
                        onClick = {},
                        style = ButtonStyle.Text,
                        enabled = false,
                    )
                },
                SampleFrame("primary_loading", themes = FrameThemes.LightOnly) {
                    AnteButton("Record expense", onClick = {}, loading = true)
                },
                SampleFrame("primary_font_2x", themes = FrameThemes.LightOnly, fontScale = 2f) {
                    AnteButton("Record expense", onClick = {})
                },
            ),
    )
