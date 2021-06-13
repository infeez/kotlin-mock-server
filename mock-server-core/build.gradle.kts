plugins {
    id("java-library")
    id("kotlin")
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    testImplementation(Dependencies.kohttp)
    testImplementation(Dependencies.gson)
}