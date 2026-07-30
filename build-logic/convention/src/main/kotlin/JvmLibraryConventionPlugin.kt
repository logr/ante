import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

/**
 * Archetype for plain JVM library modules -- code with no Android dependency, such as
 * `:core:model`, whose tests run as ordinary JVM tests rather than under Robolectric.
 *
 * Unlike the Android archetypes, this applies the standalone Kotlin JVM plugin: AGP 9's built-in
 * Kotlin only exists inside Android modules. The plugin resolves because the root build declares it
 * with `apply false`.
 *
 * Java compatibility is pinned to 17 to match `configureKotlinAndroid`, so bytecode from these
 * modules is consumable by every Android module. Here `jvmTarget` must be set explicitly -- the AGP
 * inference from `compileOptions` does not apply outside Android.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            // The Kotlin JVM plugin only brings `java`; `java-library` adds the api/implementation
            // split, which matters once other core modules re-expose types from this one.
            pluginManager.apply("java-library")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            extensions.configure<KotlinJvmExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            // kotlin-test resolves to its junit5 variant because of useJUnitPlatform() below,
            // which is what makes kotlin.test asserts land on the JUnit Platform runner. The
            // version is coerced by KGP to match the Kotlin plugin, so none is declared here.
            dependencies {
                "testImplementation"(kotlin("test"))
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
        }
    }
}
