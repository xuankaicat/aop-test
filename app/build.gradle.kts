import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    ksp(project(":library-ksp"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}