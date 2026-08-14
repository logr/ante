plugins {
    id("ante.android.application")
    id("ante.android.compose")
}

// compileSdk, minSdk, targetSdk, Java 17 compatibility and core library desugaring all come
// from the ante.android.application convention plugin.
android {
    namespace = "io.appkitchen.ante.catalog"

    defaultConfig {
        applicationId = "io.appkitchen.ante.catalog"
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    // The design system is the catalog's only project dependency, per DESIGN.md 4.1. Reaching
    // into core:model or a feature module would make the catalog a second app rather than a
    // mirror of what the design system can render on its own.
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.activity.compose)
}
