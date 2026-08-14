package io.appkitchen.ante.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Holds the Kotlin token layer to the design sheet it claims to come from.
 *
 * `tokens.json` is a copy of the design project's token sheet, vendored so the values in this
 * module have a visible source. Nothing generates Kotlin from it, so without this test the copy
 * would be free to drift and would eventually be a confident lie. The point here is that the two
 * cannot disagree silently.
 *
 * The completeness assertions matter as much as the equality ones: a token added to the sheet that
 * nothing in Kotlin reads should fail here rather than be quietly ignored.
 */
class TokenConformanceTest {

    @Test fun lightColorScheme_matchesTokenSheet() = assertScheme("light", AnteLightColorScheme)

    @Test fun darkColorScheme_matchesTokenSheet() = assertScheme("dark", AnteDarkColorScheme)

    @Test
    fun moneyColors_matchTokenSheet() {
        val money = tokens.obj("color").obj("money")
        for ((toneName, tone) in NAMED_TONES) {
            for ((themeName, colors) in
                listOf("light" to AnteLightMoneyColors, "dark" to AnteDarkMoneyColors)) {
                val expected = money.obj(toneName).obj(themeName)
                val actual = colors.forTone(tone)
                assertEquals(
                    "$toneName/$themeName content",
                    hex(expected.str("content")),
                    actual.content,
                )
                assertEquals(
                    "$toneName/$themeName container",
                    hex(expected.str("container")),
                    actual.container,
                )
                assertEquals(
                    "$toneName/$themeName onContainer",
                    hex(expected.str("onContainer")),
                    actual.onContainer,
                )
            }
        }
    }

    /**
     * Neutral is the one tone the sheet does not define - it is the "no balance meaning" case. It
     * is derived from the color scheme rather than invented, and this pins that derivation so it
     * cannot drift into a hand-picked color.
     */
    @Test
    fun neutralMoneyTone_isDerivedFromSurface() {
        for ((scheme, colors) in
            listOf(
                AnteLightColorScheme to AnteLightMoneyColors,
                AnteDarkColorScheme to AnteDarkMoneyColors,
            )) {
            val neutral = colors.forTone(MoneyTone.Neutral)
            assertEquals("neutral content is onSurface", scheme.onSurface, neutral.content)
            assertEquals(
                "neutral container is surfaceContainerHighest",
                scheme.surfaceContainerHighest,
                neutral.container,
            )
            assertEquals("neutral onContainer is onSurface", scheme.onSurface, neutral.onContainer)
        }
    }

    @Test
    fun moneyToneCues_matchTokenSheet() {
        // The sheet states the differentiator in prose rather than as data, so this asserts the
        // load-bearing parts of that sentence: the prefixes, and that owed is heavier than owing.
        val stated = tokens.obj("color").obj("money").str("nonColorDifferentiator")
        assertTrue("sheet should state the '+' prefix for owed", stated.contains("'+' prefix"))
        assertTrue("sheet should state a true minus for owing", stated.contains("'−' prefix"))

        assertEquals("+", AnteMoneyColors.cueFor(MoneyTone.Owed).prefix)
        assertEquals("−", AnteMoneyColors.cueFor(MoneyTone.Owing).prefix)
        assertTrue(
            "owed must be heavier than owing so weight carries meaning without color",
            AnteMoneyColors.cueFor(MoneyTone.Owed).weight.weight >
                AnteMoneyColors.cueFor(MoneyTone.Owing).weight.weight,
        )
    }

    @Test
    fun spacing_matchesTokenSheet() {
        val spacing = tokens.obj("spacing")
        val scale = spacing["scale"]!!.jsonArray.map { dp(it.jsonPrimitive.content) }
        val s = AnteSpacing.Default
        assertEquals(
            "raw spacing scale",
            scale,
            listOf(s.xxs, s.xs, s.sm, s.md, s.lg, s.xl, s.xxl, s.xxxl).map { it.value },
        )

        val aliases = spacing.obj("aliases")
        val actual =
            mapOf(
                "screenHorizontal" to s.screenHorizontal,
                "listRowVertical" to s.listRowVertical,
                "relatedGap" to s.relatedGap,
                "sectionGap" to s.sectionGap,
                "inlineGap" to s.inlineGap,
                "actionGap" to s.actionGap,
                "minTouchTarget" to s.minTouchTarget,
            )
        assertEquals("every alias in the sheet is modelled", aliases.keys, actual.keys)
        for ((name, value) in actual) {
            assertEquals(name, dp(aliases.str(name)), value.value, 0f)
        }
    }

    @Test
    fun shape_matchesTokenSheet() {
        val shape = tokens.obj("shape")
        val actual =
            mapOf(
                "cornerExtraSmall" to AnteShapes.extraSmall,
                "cornerSmall" to AnteShapes.small,
                "cornerMedium" to AnteShapes.medium,
                "cornerLarge" to AnteShapes.large,
                "cornerExtraLarge" to AnteShapes.extraLarge,
            )
        assertEquals("every corner in the sheet is modelled", shape.keys, actual.keys)
        for ((name, value) in actual) {
            assertEquals(name, dp(shape.str(name)), cornerDp(value), 0f)
        }
    }

    @Test
    fun moneyTextStyles_matchTokenSheet() {
        val money = tokens.obj("typography").obj("money")
        val styles = AnteTextStyles.Default
        val actual =
            mapOf(
                "moneyLarge" to styles.moneyLarge,
                "moneyMedium" to styles.moneyMedium,
                "moneySmall" to styles.moneySmall,
            )
        assertEquals("every money style in the sheet is modelled", money.keys, actual.keys)
        for ((name, style) in actual) {
            val spec = money.obj(name)
            assertEquals("$name size", sp(spec.str("size")), style.fontSize.value, 0f)
            assertEquals("$name lineHeight", sp(spec.str("lineHeight")), style.lineHeight.value, 0f)
            assertEquals(
                "$name letterSpacing",
                sp(spec.str("letterSpacing")),
                style.letterSpacing.value,
                0f,
            )
            assertEquals(
                "$name weight",
                spec["weight"]!!.jsonPrimitive.content.toInt(),
                style.fontWeight?.weight,
            )
            assertEquals("$name must set tabular figures", "tnum", style.fontFeatureSettings)
        }
    }

    /**
     * The sheet's typography policy is "no role overrides", so the Material default must be used.
     */
    @Test
    fun typography_isUnmodifiedMaterialDefault() {
        assertTrue(
            "sheet should override no roles",
            tokens.obj("typography").obj("overriddenRoles").isEmpty(),
        )
        assertEquals(Typography(), AnteTypography)
    }

    @Test
    fun elevation_matchesTokenSheet() {
        val elevation = tokens.obj("elevation")
        val actual =
            mapOf(
                "level0" to AnteElevation.level0,
                "level1" to AnteElevation.level1,
                "level2" to AnteElevation.level2,
                "level3" to AnteElevation.level3,
            )
        for ((name, value) in actual) {
            assertEquals(name, dp(elevation.str(name)), value.value, 0f)
        }
    }

    private fun assertScheme(theme: String, scheme: ColorScheme) {
        val expected = tokens.obj("color").obj("schemes").obj(theme)
        val actual = slots(scheme)

        val unaccounted = expected.keys - actual.keys - UNMAPPED_SLOTS
        assertTrue(
            "token sheet has $theme slots with no Compose equivalent modelled: $unaccounted",
            unaccounted.isEmpty(),
        )
        val missingFromSheet = actual.keys - expected.keys
        assertTrue(
            "modelled $theme slots absent from the token sheet: $missingFromSheet",
            missingFromSheet.isEmpty(),
        )

        for ((name, color) in actual) {
            assertEquals("$theme.$name", hex(expected.str(name)), color)
        }
    }

    private companion object {
        val tokens: JsonObject by lazy {
            val text =
                TokenConformanceTest::class.java.getResourceAsStream("/tokens.json")?.use {
                    it.readBytes().decodeToString()
                } ?: error("tokens.json missing from test resources")
            Json.parseToJsonElement(text).jsonObject
        }

        /**
         * Material 3's ColorScheme has no shadow slot - shadow color is not themeable in Compose -
         * so the sheet's value has nowhere to go. Listed explicitly so the completeness check stays
         * strict for everything else.
         */
        val UNMAPPED_SLOTS = setOf("shadow")

        /**
         * Neutral is excluded: the sheet does not define it. See
         * [neutralMoneyTone_isDerivedFromSurface].
         */
        val NAMED_TONES =
            listOf(
                "owed" to MoneyTone.Owed,
                "owing" to MoneyTone.Owing,
                "settled" to MoneyTone.Settled,
                "pending" to MoneyTone.Pending,
            )

        fun slots(s: ColorScheme): Map<String, Color> =
            mapOf(
                "primary" to s.primary,
                "onPrimary" to s.onPrimary,
                "primaryContainer" to s.primaryContainer,
                "onPrimaryContainer" to s.onPrimaryContainer,
                "inversePrimary" to s.inversePrimary,
                "secondary" to s.secondary,
                "onSecondary" to s.onSecondary,
                "secondaryContainer" to s.secondaryContainer,
                "onSecondaryContainer" to s.onSecondaryContainer,
                "tertiary" to s.tertiary,
                "onTertiary" to s.onTertiary,
                "tertiaryContainer" to s.tertiaryContainer,
                "onTertiaryContainer" to s.onTertiaryContainer,
                "error" to s.error,
                "onError" to s.onError,
                "errorContainer" to s.errorContainer,
                "onErrorContainer" to s.onErrorContainer,
                "background" to s.background,
                "onBackground" to s.onBackground,
                "surface" to s.surface,
                "onSurface" to s.onSurface,
                "surfaceVariant" to s.surfaceVariant,
                "onSurfaceVariant" to s.onSurfaceVariant,
                "outline" to s.outline,
                "outlineVariant" to s.outlineVariant,
                "scrim" to s.scrim,
                "inverseSurface" to s.inverseSurface,
                "inverseOnSurface" to s.inverseOnSurface,
                "surfaceDim" to s.surfaceDim,
                "surfaceBright" to s.surfaceBright,
                "surfaceContainerLowest" to s.surfaceContainerLowest,
                "surfaceContainerLow" to s.surfaceContainerLow,
                "surfaceContainer" to s.surfaceContainer,
                "surfaceContainerHigh" to s.surfaceContainerHigh,
                "surfaceContainerHighest" to s.surfaceContainerHighest,
                "surfaceTint" to s.surfaceTint,
                "primaryFixed" to s.primaryFixed,
                "onPrimaryFixed" to s.onPrimaryFixed,
                "primaryFixedDim" to s.primaryFixedDim,
                "onPrimaryFixedVariant" to s.onPrimaryFixedVariant,
                "secondaryFixed" to s.secondaryFixed,
                "onSecondaryFixed" to s.onSecondaryFixed,
                "secondaryFixedDim" to s.secondaryFixedDim,
                "onSecondaryFixedVariant" to s.onSecondaryFixedVariant,
                "tertiaryFixed" to s.tertiaryFixed,
                "onTertiaryFixed" to s.onTertiaryFixed,
                "tertiaryFixedDim" to s.tertiaryFixedDim,
                "onTertiaryFixedVariant" to s.onTertiaryFixedVariant,
            )

        fun JsonObject.obj(key: String): JsonObject =
            this[key]?.jsonObject ?: error("tokens.json is missing object '$key'")

        fun JsonObject.str(key: String): String =
            this[key]?.jsonPrimitive?.content ?: error("tokens.json is missing value '$key'")

        fun hex(value: String): Color =
            Color((0xFF000000L or value.removePrefix("#").toLong(16)).toInt())

        fun dp(value: String): Float = value.removeSuffix("dp").toFloat()

        fun sp(value: String): Float = value.removeSuffix("sp").toFloat()

        /** Density 1 makes px and dp numerically equal, so the corner reads back in dp. */
        fun cornerDp(shape: CornerBasedShape): Float =
            shape.topStart.toPx(Size(1000f, 1000f), Density(1f))
    }
}
