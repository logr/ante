plugins {
    id("ante.android.library")
    id("ante.android.compose")
}

// compileSdk, minSdk, Java 17 compatibility and core library desugaring all come from the
// ante.android.library convention plugin.
android { namespace = "io.appkitchen.ante.core.designsystem" }

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.serialization.json)
}
