package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.appkitchen.ante.core.designsystem.hct.HctSolver
import java.util.zip.CRC32

/** The two colours an initials avatar is drawn with. */
@Immutable data class AvatarColors(val container: Color, val content: Color)

/**
 * The avatar colour rule from the handoff spec (§3.5), as code.
 *
 * `hue = crc32(utf8(id)) mod 360`, then `HCT(hue, chroma 16, tone N)` with the tones below. Keyed
 * to the member id rather than the name, so a rename never recolours anyone. Chroma 16 keeps every
 * hue muted enough to sit beside the money tones without competing; the 80-point tone gap between
 * container and content is what makes the contrast guarantee - HCT tone is L*, and 80 points of L*
 * is well over 4.5:1 at any hue. `AvatarColorsTest` measures that for all 360 hues rather than
 * trusting the arithmetic.
 *
 * Tone is exact by construction; when a hue cannot reach chroma 16 at a tone the solver reduces
 * chroma and keeps the tone, so the guarantee survives gamut clamping too.
 *
 * Computed rather than tabulated like the other token families, which is why it carries the theme
 * axis itself instead of being two transcribed instances.
 */
@Immutable
class AnteAvatarColors internal constructor(private val darkTheme: Boolean) {

    fun forMember(memberId: String): AvatarColors = forHue(hueFor(memberId))

    fun forHue(hue: Int): AvatarColors {
        val (containerTone, contentTone) =
            if (darkTheme) {
                DARK_CONTAINER_TONE to DARK_CONTENT_TONE
            } else {
                LIGHT_CONTAINER_TONE to LIGHT_CONTENT_TONE
            }
        return AvatarColors(
            container = Color(HctSolver.solveToInt(hue.toDouble(), CHROMA, containerTone)),
            content = Color(HctSolver.solveToInt(hue.toDouble(), CHROMA, contentTone)),
        )
    }

    companion object {
        private const val CHROMA = 16.0
        private const val LIGHT_CONTAINER_TONE = 90.0
        private const val LIGHT_CONTENT_TONE = 10.0
        private const val DARK_CONTAINER_TONE = 30.0
        private const val DARK_CONTENT_TONE = 90.0

        internal val Light = AnteAvatarColors(darkTheme = false)
        internal val Dark = AnteAvatarColors(darkTheme = true)

        /** Stable hue for a member id, in degrees. */
        fun hueFor(memberId: String): Int {
            val crc = CRC32().apply { update(memberId.encodeToByteArray()) }
            return (crc.value % 360).toInt()
        }
    }
}

/** No default, for the same reason as [LocalAnteMoneyColors]. */
val LocalAnteAvatarColors =
    staticCompositionLocalOf<AnteAvatarColors> {
        error("AnteAvatarColors not provided - wrap the call site in AnteTheme")
    }
