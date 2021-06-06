group = "kotlin-mock-server"
version = "1.0.0-alpha"

val ktlint by configurations.creating

plugins {
    java
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.dokka") version "1.4.32"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.squareup.okhttp3", "mockwebserver", "4.2.1")
    implementation("io.netty", "netty-codec-http", "4.1.59.Final")
    implementation("io.netty", "netty-transport-native-epoll", "4.1.59.Final")

    testImplementation("io.github.rybalkinsd", "kohttp", "0.12.0")
    testImplementation("com.google.code.gson", "gson", "2.8.6")
    testImplementation(kotlin("test-junit"))

    ktlint("com.pinterest:ktlint:0.41.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

tasks.compileJava {
    options.isIncremental = true
    options.isFork = true
    options.isFailOnError = false
}

tasks.wrapper {
    gradleVersion = "7.0"
}

//fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ""
                password = ""
            }
        }
    }
    publications.withType<MavenPublication> {
        //artifact(javadocJar.get())
        pom {
            name.set("Kotlin Mock Server")
            description.set("Kotlin mock server for testing any client.")
            url.set("https://github.com/infeez/kotlin-mock-server")

            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://opensource.org/licenses/Apache-2.0")
                }
            }
            developers {
                developer {
                    id.set("infeez")
                    name.set("Vadim Vasyanin")
                    email.set("infeez@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/infeez/kotlin-mock-server")
            }
        }
    }
}
