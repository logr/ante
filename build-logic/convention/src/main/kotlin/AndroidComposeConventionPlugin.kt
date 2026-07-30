import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Opt-in Compose support, kept separate from the module-archetype plugins so that modules without
 * any UI do not pay the Compose compiler's per-module build cost.
 *
 * Reads the Android extension generically rather than requiring an application or library
 * extension, so this works unchanged on library modules.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // Resolvable because the root build declares this plugin with `apply false`, which
            // places it on the classpath without applying it to the root project.
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            // Property access rather than a `buildFeatures { }` block: on AGP 9's non-generic
            // CommonExtension only the getter exists.
            extensions.getByType<CommonExtension>().buildFeatures.compose = true

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))

                add("implementation", libs.findLibrary("androidx-compose-ui").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                add("implementation", libs.findLibrary("androidx-compose-material3").get())

                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
}
