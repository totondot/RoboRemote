// File: RoboRemote/build.gradle.kts (Project Level)
plugins {
    // UPDATED: Changed from 8.1.0 to 8.9.1 to fix the dependency errors
    id("com.android.application") version "8.13.2" apply false

    // UPDATED: Changed from 1.8.10 to 2.0.20 to be compatible with the new Android version
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false

    // Google services (Firebase) remains the same
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.compose.compiler) apply false

}