plugins {
    id("java-library")
    id("kotlin")
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


    implementation("io.netty", "netty-codec-http", "4.1.59.Final")
    implementation("io.netty", "netty-transport-native-epoll", "4.1.59.Final")
}