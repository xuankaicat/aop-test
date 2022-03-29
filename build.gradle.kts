import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("com.google.devtools.ksp") version "1.6.10-1.0.4" apply false
}

allprojects {
    group = "com.github.xuankaicat"
    version = "1.0-SNAPSHOT"
}