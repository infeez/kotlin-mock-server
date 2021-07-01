plugins {
    id("kotlin")
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":mock-server-core"))
    implementation(project(":mock-server-okhttp"))
    implementation(project(":mock-server-junit4"))

    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation(Dependencies.kohttp)
    implementation(Dependencies.gson)

    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.mockitoKotlin)
}
