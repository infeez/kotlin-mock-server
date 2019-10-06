plugins {
    java
    kotlin("jvm") version "1.3.50"
    id("maven")
}

group = "kotlin-mock-server"
version = "0.2.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.squareup.okhttp3", "mockwebserver", "4.2.1")

    testImplementation("io.github.rybalkinsd", "kohttp", "0.11.0")
    testImplementation(kotlin("test-junit"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Jar> {
    archiveName = "${project.group}-$version.jar"
    from(configurations.compile.map { if (it.isDirectory) it else zipTree(it) })
}