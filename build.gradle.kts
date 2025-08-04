// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrainsKotlinSerialization) apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}