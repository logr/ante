package io.appkitchen.ante.core.designsystem.samples

import io.appkitchen.ante.core.designsystem.component.AnteBanner
import io.appkitchen.ante.core.designsystem.component.BannerKind

/**
 * Spec §3.8 screenshots: Info + Error, light + dark; one Error wrapped to 3 lines at 2x font scale.
 * 5 frames.
 *
 * Copy is the spec's required error strings, verbatim - these ship. Full-bleed because the banner
 * carries its own margin.
 */
val BannerSample: ComponentSample =
    ComponentSample(
        id = "banner",
        title = "AnteBanner",
        frames =
            listOf(
                SampleFrame("info", fullBleed = true) {
                    AnteBanner(
                        "A member appears twice in this balance set, so a plan can't be drawn. " +
                            "It should clear when this device finishes syncing."
                    )
                },
                SampleFrame("error", fullBleed = true) {
                    AnteBanner(
                        "Shares must add up to $42.00. $3.50 left to assign.",
                        kind = BannerKind.Error,
                    )
                },
                SampleFrame(
                    "error_wrapped_font_2x",
                    themes = FrameThemes.LightOnly,
                    fontScale = 2f,
                    fullBleed = true,
                ) {
                    AnteBanner(
                        "Percentages must add up to exactly 100.00%. Remove 12.50%.",
                        kind = BannerKind.Error,
                    )
                },
            ),
    )
