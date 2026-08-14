// AGP is declared `compileOnly` in build-logic so that the real version stays under this
// build's control. That means AGP is absent from the convention plugins' *runtime* classpath
// unless this root build loads it -- without the `apply false` alias below, applying
// `ante.android.application` fails with NoClassDefFoundError on ApplicationExtension.
// `android.library` resolves to the same com.android.tools.build:gradle artifact as
// `android.application`, so it is redundant today. It is listed anyway: this block is meant to
// read as an honest inventory of what the convention plugins apply, and it survives AGP ever
// splitting the artifact.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
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
}
