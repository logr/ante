plugins {
    id("ante.android.application")
    id("ante.android.compose")
}

// compileSdk, minSdk, targetSdk, Java 17 compatibility and core library desugaring all come
// from the ante.android.application convention plugin.
android {
    namespace = "io.appkitchen.ante"

    defaultConfig {
        applicationId = "io.appkitchen.ante"
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
}
