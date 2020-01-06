group = "kotlin-mock-server"
version = "0.3.0"

plugins {
    java
    kotlin("jvm") version "1.3.61"
    id("maven")
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

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
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    filter {
        exclude("**/style-violations.kt")
    }
}

tasks.withType<Jar> {
    archiveName = "${project.group}-$version.jar"
}
