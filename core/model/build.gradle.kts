plugins {
    id("ante.jvm.library")
}

dependencies {
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
}
