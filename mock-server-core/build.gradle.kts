plugins {
    id("java-library")
    id("kotlin")
    `kotlin-dsl`
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
    testImplementation(Dependencies.gson)
    testImplementation(Dependencies.kotlinTest)
}
