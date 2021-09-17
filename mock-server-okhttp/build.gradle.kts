plugins {
    id("java-library")
    id("kotlin")
    id("com.vanniktech.maven.publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":mock-server-core"))

    implementation(Dependencies.okhttp3MockWebServer)
}
