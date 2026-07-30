import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal const val ANTE_COMPILE_SDK = 37
internal const val ANTE_TARGET_SDK = 37
internal const val ANTE_MIN_SDK = 26

/**
 * Configuration shared by every Android module archetype.
 *
 * Typed against [CommonExtension] so the same function serves application and library modules.
 *
 * Two AGP 9 details shape how this is written:
 * - [CommonExtension] takes no type parameters at all. Older examples write `CommonExtension<*, *,
 *   *, *, *, *>`, which no longer compiles.
 * - [CommonExtension] exposes only getters. The lambda-accepting DSL overloads (`defaultConfig {
 *   }`, `compileOptions { }`) are declared on the concrete extension types such as
 *   `ApplicationExtension`, so configuration here goes through property access rather than nested
 *   blocks.
 *
 * Kotlin's `jvmTarget` is intentionally not set: AGP 9 infers it from
 * `compileOptions.targetCompatibility`.
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    with(commonExtension) {
        compileSdk = ANTE_COMPILE_SDK
        defaultConfig.minSdk = ANTE_MIN_SDK

        compileOptions.sourceCompatibility = JavaVersion.VERSION_17
        compileOptions.targetCompatibility = JavaVersion.VERSION_17
        compileOptions.isCoreLibraryDesugaringEnabled = true
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("desugar-jdk-libs").get())
    }
}
