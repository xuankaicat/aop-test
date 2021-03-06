plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))

    compileOnly("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    // https://mvnrepository.com/artifact/com.squareup/kotlinpoet
    implementation("com.squareup:kotlinpoet:1.11.0")

    compileOnly(kotlin("compiler-embeddable"))
    // https://mvnrepository.com/artifact/com.google.devtools.ksp/symbol-processing-api
    compileOnly("com.google.devtools.ksp:symbol-processing-api:1.6.10-1.0.4")
    implementation(kotlin("reflect"))
}