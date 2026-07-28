// AGP is declared `compileOnly` in build-logic so that the real version stays under this
// build's control. That means AGP is absent from the convention plugins' *runtime* classpath
// unless this root build loads it -- without the `apply false` alias below, applying
// `ante.android.application` fails with NoClassDefFoundError on ApplicationExtension.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.spotless)
}

val ktlintVersion = libs.versions.ktlint.get()

// Passed explicitly rather than relied upon from .editorconfig: Spotless's ktlint step does
// not pick up the root .editorconfig here, so these would silently have no effect. The same
// keys are mirrored in .editorconfig so the IDE and ktlint CLI agree with the build.
val ktlintOverrides =
    mapOf(
        // @Composable functions are PascalCase by convention, which standard:function-naming
        // would otherwise reject on every composable in the project.
        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
    )

spotless {
    // Targets are set by path rather than by source set: this root build has no Kotlin
    // plugin, and path targeting also reaches build-logic/, which is a separate Gradle build.
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint(ktlintVersion).editorConfigOverride(ktlintOverrides)
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**")
        ktlint(ktlintVersion).editorConfigOverride(ktlintOverrides)
    }
}
