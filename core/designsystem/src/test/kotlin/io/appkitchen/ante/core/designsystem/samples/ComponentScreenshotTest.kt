package io.appkitchen.ante.core.designsystem.samples

import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The spec's §3 screenshot matrix, one capture per frame per theme.
 *
 * Driven by [AnteSamples] rather than hand-listed so the test cannot fall behind the samples: a new
 * frame is a new golden with no test edit, and the catalog shows exactly what verify checks.
 * `SampleMatrixTest` is what holds the sample list itself to the spec's counts.
 *
 * Parameters are primitives (component id, frame name, theme) and the frame is looked up inside the
 * test rather than passed in, so nothing crosses Robolectric's sandbox classloader boundary as an
 * object. The parameter name is the golden's stem, so a red test names its file.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-xhdpi-keyshidden-nonav")
class ComponentScreenshotTest(
    private val golden: String,
    private val componentId: String,
    private val frameName: String,
    private val dark: Boolean,
) {

    @Test
    fun frame() {
        val component = AnteSamples.components.first { it.id == componentId }
        val frame = component.frames.first { it.name == frameName }
        captureRoboImage("$ScreenshotDir/$golden.png", roborazziOptions = ExactMatch) {
            AnteTheme(darkTheme = dark) { frame.Render() }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun frames(): List<Array<Any>> =
            AnteSamples.components.flatMap { component ->
                component.frames.flatMap { frame ->
                    val themes =
                        when (frame.themes) {
                            FrameThemes.Both -> listOf(false, true)
                            FrameThemes.LightOnly -> listOf(false)
                        }
                    themes.map { dark ->
                        val theme = if (dark) "dark" else "light"
                        arrayOf<Any>(
                            "${component.id}_${frame.name}_$theme",
                            component.id,
                            frame.name,
                            dark,
                        )
                    }
                }
            }

        /**
         * See TokenSampleScreenshotTest: exact match against the pinned runner's render. A getter
         * rather than a value because [frames] runs this companion's initializer outside the
         * Robolectric sandbox, and RoborazziOptions cannot be built there.
         */
        private val ExactMatch: RoborazziOptions
            get() =
                RoborazziOptions(
                    compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f)
                )

        /**
         * Set by ante.android.screenshot; deliberately not defaulted (see
         * TokenSampleScreenshotTest).
         */
        private val ScreenshotDir: String
            get() =
                requireNotNull(System.getProperty("ante.screenshotDir")) {
                    "ante.screenshotDir is unset, run screenshot tests through Gradle."
                }
    }
}
