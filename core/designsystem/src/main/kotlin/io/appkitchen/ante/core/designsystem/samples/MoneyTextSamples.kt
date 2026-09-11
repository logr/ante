package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteMoneyText
import io.appkitchen.ante.core.designsystem.component.MoneyStyle
import io.appkitchen.ante.core.designsystem.theme.MoneyTone

/**
 * Spec §3.2 screenshots: 5 tones at Medium, light + dark; voided; Large at 2x font scale. 13
 * frames.
 *
 * The strings are what the §2 formatter would emit for each tone - explicit "+", true minus, two
 * decimals, grouped - written out because the component does not format and the formatter lives in
 * `core:ui`.
 */
val MoneyTextSample: ComponentSample =
    ComponentSample(
        id = "money_text",
        title = "AnteMoneyText",
        frames =
            listOf(
                SampleFrame("owed_medium") { AnteMoneyText("+$42.50", tone = MoneyTone.Owed) },
                SampleFrame("owing_medium") { AnteMoneyText("−$18.25", tone = MoneyTone.Owing) },
                SampleFrame("settled_medium") { AnteMoneyText("$0.00", tone = MoneyTone.Settled) },
                SampleFrame("pending_medium") {
                    AnteMoneyText("$20.00", tone = MoneyTone.Pending)
                },
                SampleFrame("neutral_medium") { AnteMoneyText("$48.00", tone = MoneyTone.Neutral) },
                SampleFrame("voided") {
                    AnteMoneyText("$42.00", tone = MoneyTone.Neutral, voided = true)
                },
                SampleFrame("large_font_2x", themes = FrameThemes.LightOnly, fontScale = 2f) {
                    AnteMoneyText(
                        "−$1,204,517.30",
                        tone = MoneyTone.Owing,
                        style = MoneyStyle.Large,
                    )
                },
            ),
    )
