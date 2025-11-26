// build.gradle.kts (Project)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // ✅ NUEVO: Hilt
    id("com.google.dagger.hilt.android") version "2.51" apply false
}