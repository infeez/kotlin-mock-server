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
    api(Dependencies.junit4)

    testImplementation(project(":mock-server-okhttp"))
}