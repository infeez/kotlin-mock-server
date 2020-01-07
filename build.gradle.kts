group = "kotlin-mock-server"
version = "0.3.0"

plugins {
    java
    kotlin("jvm") version "1.3.61"
    id("maven")
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.squareup.okhttp3", "mockwebserver", "4.2.1")

    testImplementation("io.github.rybalkinsd", "kohttp", "0.11.1")
    testImplementation(kotlin("test-junit"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    filter {
        exclude("**/style-violations.kt")
    }
}

tasks.compileJava {
    options.isIncremental = true
    options.isFork = true
    options.isFailOnError = false
}
