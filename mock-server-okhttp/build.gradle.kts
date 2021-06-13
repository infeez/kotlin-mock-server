plugins {
    id("java-library")
    id("kotlin")
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
    api(project(":mock-server-core"))

    implementation("com.squareup.okhttp3", "mockwebserver", "4.2.1")
}