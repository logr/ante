package io.appkitchen.ante.core.designsystem.samples

import androidx.compose.runtime.Composable
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import io.appkitchen.ante.core.designsystem.theme.AnteTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Spike coverage for the screenshot pipeline itself, not for the tokens.
 *
 * The point of these captures is that goldens exist and that corrupting one turns the build red.
 * Component goldens arrive with the components.
 *
 * One capture per section rather than one of the whole sample: the capture is bounded by the device
 * height, so a composable taller than the screen is silently cropped and the golden then covers
 * only what happened to fit. Sections are also a better review unit - a diff points at the token
 * family that changed.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-xhdpi-keyshidden-nonav")
class TokenSampleScreenshotTest {

    @Test fun colorRoles_light() = capture("color_roles_light") { ColorRoleSample() }

    @Test fun colorRoles_dark() = capture("color_roles_dark", dark = true) { ColorRoleSample() }

    @Test fun moneyTones_light() = capture("money_tones_light") { MoneyToneSample() }

    @Test fun moneyTones_dark() = capture("money_tones_dark", dark = true) { MoneyToneSample() }

    @Test fun typeRamp_light() = capture("type_ramp_light") { TypeRampSample() }

    @Test fun typeRamp_dark() = capture("type_ramp_dark", dark = true) { TypeRampSample() }

    @Test fun spacing_light() = capture("spacing_light") { SpacingSample() }

    @Test fun spacing_dark() = capture("spacing_dark", dark = true) { SpacingSample() }

    private fun capture(name: String, dark: Boolean = false, content: @Composable () -> Unit) {
        captureRoboImage("$ScreenshotDir/$name.png", roborazziOptions = ExactMatch) {
            AnteTheme(darkTheme = dark) { content() }
        }
    }

    private companion object {
        /**
         * Roborazzi's default `changeThreshold` is 1%, which on an 822x762 capture lets roughly six
         * thousand pixels change without failing - a whole component-sized region. A threshold that
         * hides platform drift hides real regressions too, so the answer is an exact match plus
         * goldens recorded on one platform, not a tolerance.
         */
        val ExactMatch =
            RoborazziOptions(compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f))

        /**
         * Set by ante.android.screenshot. Deliberately not defaulted: a fallback literal would
         * write goldens to a directory the Test task is not watching, and the run would pass.
         */
        val ScreenshotDir: String =
            requireNotNull(System.getProperty("ante.screenshotDir")) {
                "ante.screenshotDir is unset, run screenshot tests through Gradle."
            }
    }
}
