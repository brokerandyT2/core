pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
         maven("https://jitpack.io") 
    }
}

rootProject.name = "CoreApp"
include(":app")
include(":core")
include(":photography") 

// Enable Kotlin Multiplatform Gradle plugin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// /settings.gradle.kts