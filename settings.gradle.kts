pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

rootProject.name = "aop-test"
include("library")
include("library-ksp")
include("app")
