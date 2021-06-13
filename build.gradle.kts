buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    }
}

plugins {
    id("org.jetbrains.dokka") version "1.4.32"
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}