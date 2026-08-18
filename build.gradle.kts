// AGP is declared `compileOnly` in build-logic so that the real version stays under this build's
// control. That means AGP is absent from the convention plugins' *runtime* classpath unless this
// root build loads it. Without the `apply false` aliases below, applying `ante.android.application`
// fails with NoClassDefFoundError on ApplicationExtension.
//
// One alias per external plugin the convention plugins apply. `android.library` resolves to the
// same com.android.tools.build:gradle artifact as `android.application` and so adds nothing to the
// classpath, but it is listed to keep that correspondence exact.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.spotless)
}

val ktfmtVersion = libs.versions.ktfmt.get()

spotless {
    // Targets are set by path rather than by source set: this root build has no Kotlin
    // plugin, and path targeting also reaches build-logic/, which is a separate Gradle build.
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktfmt(ktfmtVersion).kotlinlangStyle()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**")
        ktfmt(ktfmtVersion).kotlinlangStyle()
    }
    json {
        target("**/*.json")
        // Spotless is not gitignore-aware: without these it rewrites the untracked
        // agent-skill JSON under .claude/ and any IDE/Gradle metadata.
        targetExclude("**/build/**", ".idea/**", ".gradle/**")
        // gson preserves key order (tokens.json's ordering is meaningful) and splits every
        // property onto its own line, matching IntelliJ's JSON defaults. Indent must be set
        // explicitly — Spotless defaults to 4, .editorconfig says 2.
        gson().indentWithSpaces(2)
    }
}
