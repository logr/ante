import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            // No Kotlin plugin is applied: AGP 9 compiles Kotlin itself, and applying
            // org.jetbrains.kotlin.android here fails with "Cannot add extension with name 'kotlin'".
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                // targetSdk is application-only, so it cannot live in the shared helper.
                defaultConfig.targetSdk = ANTE_TARGET_SDK
            }
        }
}
