package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The spec claims "the 80-point tone gap at chroma 16 keeps every hue >= 4.5:1". This measures it
 * with the WCAG contrast formula for every hue in both themes rather than trusting the L*
 * arithmetic: the promise is about rendered sRGB, and gamut clamping sits between HCT and sRGB.
 */
class AvatarColorsTest {

    @Test fun everyHue_meetsContrastFloor_light() = assertAllHues(AnteAvatarColors.Light, "light")

    @Test fun everyHue_meetsContrastFloor_dark() = assertAllHues(AnteAvatarColors.Dark, "dark")

    /** The hue is a function of the id alone: renaming must not recolour. */
    @Test
    fun hue_isStableAndInRange() {
        for (id in listOf("", "a", "member-1", "0f9d3c2a-6b7e-4e21-9c1f-2f6a5c9d1e33", "名前")) {
            val hue = AnteAvatarColors.hueFor(id)
            assertEquals(hue, AnteAvatarColors.hueFor(id))
            assertTrue("hue $hue for '$id' out of range", hue in 0..359)
        }
        // crc32("member-1") is a fixed number; pin it so a change to the hash or the modulus is a
        // visible decision rather than a silent recolour of every avatar in every ledger.
        assertEquals(154, AnteAvatarColors.hueFor("member-1"))
    }

    private fun assertAllHues(colors: AnteAvatarColors, theme: String) {
        for (hue in 0 until 360) {
            val (container, content) = colors.forHue(hue)
            val ratio = contrast(container, content)
            assertTrue("$theme hue $hue: ${"%.2f".format(ratio)}:1 is below 4.5:1", ratio >= 4.5)
        }
    }

    private companion object {
        fun contrast(a: Color, b: Color): Double {
            val la = luminance(a)
            val lb = luminance(b)
            return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
        }

        /** WCAG 2.x relative luminance from sRGB. */
        fun luminance(c: Color): Double {
            fun channel(v: Float): Double {
                val s = v.toDouble()
                return if (s <= 0.04045) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
            }
            return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
        }
    }
}
