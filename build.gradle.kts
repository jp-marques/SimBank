// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.jetbrains.compose") version "1.8.0-alpha03"
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Add the JetBrains Compose repository
}