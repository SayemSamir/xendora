import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.sayem.notevault"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sayem.notevault"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0"
    }

    // Throwaway debug-only keystore committed on purpose. Without it, every CI
    // runner generates a new debug key, and Android refuses to install an update
    // signed by a different key, which would force an uninstall and wipe Room data.
    // Never reuse this key for a release build.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug-signing/notevault-debug.jks")
            storePassword = "notevault-debug"
            keyAlias = "notevault-debug"
            keyPassword = "notevault-debug"
        }
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Room exports the schema JSON here. Without this, exportSchema = true only warns.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.09.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Version is managed by the Compose BOM so every Compose artifact stays aligned.
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
