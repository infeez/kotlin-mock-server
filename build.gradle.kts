buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.android.tools.build:gradle:4.2.0") // IDEA issue with 4.2 - https://youtrack.jetbrains.com/issue/IDEA-268968
    }
}

plugins {
    id("org.jetbrains.dokka") version "1.4.32"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}