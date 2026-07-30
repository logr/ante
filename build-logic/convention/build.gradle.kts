import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins { `kotlin-dsl` }

group = "io.appkitchen.ante.buildlogic"

// Deliberately 17 rather than the app's 21 toolchain: these plugins run inside Gradle's own
// JVM. `kotlin-dsl` would otherwise pick a jvmTarget that disagrees with compileJava and fail
// with "Inconsistent JVM-target compatibility", so both are pinned explicitly.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    // compileOnly, never implementation: the AGP version that actually runs comes from the
    // root build's plugin resolution, not from here.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "ante.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = "ante.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = "ante.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
