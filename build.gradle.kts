// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("multiplatform") version "1.9.20" apply false
    id("com.android.library") version "8.1.2" apply false
}