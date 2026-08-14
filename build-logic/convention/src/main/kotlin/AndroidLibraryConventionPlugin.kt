import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // No Kotlin plugin is applied: AGP 9 compiles Kotlin itself, and applying
            // org.jetbrains.kotlin.android here fails with "Cannot add extension with name
            // 'kotlin'".
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                // No targetSdk, deliberately: it is application-only and has no meaning for a
                // library. The application archetype sets it; this one must not.

                // Unit tests read real resources rather than getting stubs back. Screenshot
                // tooling needs this, and so does anything asserting on a string resource.
                testOptions.unitTests.isIncludeAndroidResources = true
            }
        }
}
