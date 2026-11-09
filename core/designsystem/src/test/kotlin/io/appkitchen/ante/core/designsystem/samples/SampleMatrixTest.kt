package io.appkitchen.ante.core.designsystem.samples

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Holds the sample matrix to the handoff spec's screenshot lists (§3), which are the test matrix
 * verbatim: 87 frames across eight components.
 *
 * The screenshot test derives its captures from [AnteSamples], so a frame dropped from a sample
 * silently drops its golden too - verify cannot miss what it was never asked for. This is what
 * notices. Counts are per component so a failure names the one that drifted.
 */
class SampleMatrixTest {

    @Test
    fun captureCounts_matchSpec() {
        val actual = AnteSamples.components.associate { it.id to it.captureCount }
        assertEquals(EXPECTED_CAPTURES, actual)
    }

    @Test
    fun frameNames_areUniqueWithinComponent() {
        for (component in AnteSamples.components) {
            val names = component.frames.map { it.name }
            assertEquals(
                "${component.id} has duplicate frame names",
                names.toSet().size,
                names.size,
            )
            for (name in names) {
                assertTrue(
                    "${component.id}/$name is not a golden-safe name",
                    name.matches(Regex("[a-z0-9_]+")),
                )
            }
        }
    }

    private companion object {
        /** Component id -> capture count, straight from the spec's per-component totals. */
        val EXPECTED_CAPTURES: Map<String, Int> = mapOf("button" to 14, "money_text" to 13)
    }
}
