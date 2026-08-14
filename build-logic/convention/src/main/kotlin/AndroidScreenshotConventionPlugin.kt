import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.classpath.Instrumented.systemProperty
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * Screenshot testing, kept separate from the Compose archetype for the same reason Compose is
 * separate from the module archetypes: a module with no goldens should not pay for Robolectric.
 *
 * No extension configuration here, which is why build-logic needs no compileOnly dependency on the
 * Roborazzi plugin. Captures are written by hand rather than generated from `@Preview` scanning -
 * previews cannot express a state that needs arguments, and the component states worth capturing
 * mostly do.
 */
class AndroidScreenshotConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // Resolvable because the root build declares this plugin with `apply false`.
            pluginManager.apply("io.github.takahirom.roborazzi")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                // ante.android.compose puts the BOM on implementation and androidTestImplementation
                // only. Without it here, the unversioned ui-test-junit4 alias cannot resolve.
                add("testImplementation", platform(libs.findLibrary("androidx-compose-bom").get()))

                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("robolectric").get())
                add("testImplementation", libs.findLibrary("roborazzi").get())
                add("testImplementation", libs.findLibrary("roborazzi-compose").get())
                add("testImplementation", libs.findLibrary("roborazzi-junit-rule").get())
                add(
                    "testImplementation",
                    libs.findLibrary("androidx-compose-ui-test-junit4").get(),
                )

                add(
                    "debugImplementation",
                    libs.findLibrary("androidx-compose-ui-test-manifest").get(),
                )
            }

            // Goldens are read by the verify run, but nothing else wires them into the test
            // task's inputs. Without this, Gradle calls testDebugUnitTest UP-TO-DATE after a
            // golden changes and the comparison never runs -- verify then reports success on a
            // golden it did not look at. Declared optional so a module can adopt the archetype
            // before it has recorded anything.
            tasks.withType<Test>().configureEach {
                // The test resolves its capture paths from this rather than repeating the literal.
                // A divergence would point the input declaration below at a directory nothing
                // writes to, which is the silent version of the bug it exists to prevent.
                systemProperty("ante.screenshotDir", SCREENSHOT_DIR)

                inputs
                    .files(layout.projectDirectory.dir(SCREENSHOT_DIR).asFileTree)
                    .withPropertyName("roborazziGoldens")
                    .withPathSensitivity(PathSensitivity.RELATIVE)
                    .optional()
            }
        }

    private companion object {
        const val SCREENSHOT_DIR = "src/test/screenshots"
    }
}
