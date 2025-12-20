// File: RoboRemote/build.gradle.kts (Project Level)
// Project-level build.gradle.kts
// App-level build.gradle.kts
plugins {
    // We just list the IDs here. They inherit versions from the file above.
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
}


android {
    namespace = "com.example.roboremote"
    compileSdk = 36 // Corrected line

    defaultConfig {
        applicationId = "com.example.roboremote"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // composeOptions are needed when using the Kotlin Compose compiler plugin
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Example version, check for compatibility
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    // Firebase (Add the Google Services plugin to project first)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("androidx.compose.material:material-icons-extended:1.6.8") // Check for the latest version
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Helper for Kotlin
    ksp("androidx.room:room-compiler:$room_version")      // The compiler
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
}
