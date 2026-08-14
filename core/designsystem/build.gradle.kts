plugins {
    id("ante.android.library")
    id("ante.android.compose")
}

// compileSdk, minSdk, Java 17 compatibility and core library desugaring all come from the
// ante.android.library convention plugin.
android { namespace = "io.appkitchen.ante.core.designsystem" }

dependencies {
    // Declared as 'implementation' in the convention plugin but types from each of these is
    // exposed through the design system so we declare them as 'api' dependencies here.
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.serialization.json)
}
