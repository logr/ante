package io.appkitchen.ante.core.designsystem.samples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.appkitchen.ante.core.designsystem.theme.AnteTheme

/** Which themes a frame is captured in. The spec's screenshot lists say which per frame. */
enum class FrameThemes {
    Both,
    LightOnly,
}

/**
 * One frame of the handoff spec's screenshot matrix (§3, "Screenshots" per component).
 *
 * A frame is the unit that both the catalog and the screenshot test render, so there is one
 * rendering of every state and it cannot drift between the two. [name] is the golden file's stem
 * (`<component>_<name>_<theme>.png`) and the catalog's label, so it reads as the state it shows.
 *
 * [fontScale] exists for the "at 2x font scale" frames; the wrapper applies it, the content does
 * not know. [fullBleed] is for components that own their horizontal inset (list rows, banners) -
 * the wrapper then adds none, so the frame shows the component's real edges.
 */
@Immutable
class SampleFrame(
    val name: String,
    val themes: FrameThemes = FrameThemes.Both,
    val fontScale: Float = 1f,
    val fullBleed: Boolean = false,
    val content: @Composable () -> Unit,
) {
    /** Captures this frame produces across themes - what the count guard adds up. */
    val captureCount: Int
        get() =
            when (themes) {
                FrameThemes.Both -> 2
                FrameThemes.LightOnly -> 1
            }
}

/** A component's entry in the matrix: its frames, in the order the spec lists them. */
@Immutable
class ComponentSample(val id: String, val title: String, val frames: List<SampleFrame>) {
    val captureCount: Int
        get() = frames.sumOf { it.captureCount }
}

/**
 * Renders a frame the one way it is ever rendered.
 *
 * On a [Surface] so the frame carries the theme's surface and content colours itself - a capture is
 * otherwise drawn on the host window with the default black content colour, which makes a dark
 * frame a lie. Full width, so trailing-aligned content (amount columns) lands where it does in the
 * app rather than hugging the component's intrinsic width.
 */
@Composable
fun SampleFrame.Render(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density = density.density, fontScale = fontScale)
    ) {
        Surface(color = AnteTheme.colorScheme.surface, modifier = modifier.fillMaxWidth()) {
            val horizontal = if (fullBleed) 0.dp else AnteTheme.spacing.screenHorizontal
            Box(
                Modifier.padding(
                    horizontal = horizontal,
                    vertical = AnteTheme.spacing.listRowVertical,
                )
            ) {
                content()
            }
        }
    }
}

/**
 * The matrix, in the spec's §3 order. Each component commit appends its entry; the count guard in
 * `SampleMatrixTest` holds the totals to the spec's numbers.
 */
object AnteSamples {
    val components: List<ComponentSample> =
        listOf(
            ButtonSample,
            MoneyTextSample,
            ListRowSample,
            CardSample,
            AvatarStackSample,
            ChipSample,
            BannerSample,
        )
}
