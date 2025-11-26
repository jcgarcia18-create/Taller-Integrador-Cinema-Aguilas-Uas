// build.gradle.kts (Module: app)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)

    // ✅ NUEVO: Hilt
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.tallerintegrador"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tallerintegrador"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ========== COMPOSE ==========
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ========== CORE ANDROID ==========
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ========== HILT (DEPENDENCY INJECTION) ========== ✅ NUEVO
    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ========== RETROFIT (RED) ==========
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ========== COIL (IMÁGENES) ==========
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ========== ROOM (CACHE LOCAL) ==========
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // ========== YOUTUBE Y BROWSER ==========
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")
    implementation("androidx.browser:browser:1.7.0")

    // DEBUG
    debugImplementation("androidx.compose.ui:ui-tooling")
}