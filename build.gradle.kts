import io.gitlab.arturbosch.detekt.Detekt

val ktlint by configurations.creating

val libVersion = "1.0.2-SNAPSHOT"
val jacocoVersion = "0.8.7"
val ktlintVersion = "0.41.0"

dependencies {
    ktlint("com.pinterest:ktlint:$ktlintVersion") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:4.2.0") // IDEA issue with 4.2 - https://youtrack.jetbrains.com/issue/IDEA-268968
    }
}

plugins {
    id("org.jetbrains.dokka") version "1.4.32"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    jacoco
    java
    id("com.vanniktech.maven.publish") version "0.17.0"
}

jacoco {
    toolVersion = jacocoVersion
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt.yml")
    reports {
        html.enabled = true
    }
}

tasks.register<Detekt>("detektFull") {
    parallel = true
    autoCorrect = true
    description = "Runs a full detekt check."
    setSource(files(projectDir))
    include("**/*.kt")
    include("**/*.kts")
    exclude("resources/")
    exclude("build/")
}

allprojects {

    group = "io.github.infeez.kotlin-mock-server"
    version = System.getenv("RELEASE_VERSION") ?: libVersion

    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublish {
            sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
        }
    }

    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "jacoco")
    apply(plugin = "java")

    jacoco {
        toolVersion = jacocoVersion
    }

    tasks {
        jacocoTestReport {
            reports {
                html.isEnabled = true
                xml.isEnabled = true
            }
        }

        register<JacocoReport>("jacocoFullReport") {
            group = "verification"
            subprojects {
                plugins.withType<JacocoPlugin>().configureEach {
                    tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.configureEach {
                        if (File("${buildDir}/jacoco/test.exec").exists()) {
                            sourceSets(this@subprojects.the<SourceSetContainer>()["main"])
                            executionData(this)
                        }
                    }
                }
            }

            reports {
                xml.isEnabled = true
                xml.destination = File("${buildDir}/reports/jacoco/report/test/jacocoTestReport.xml")
                html.isEnabled = true
                html.destination = File("${buildDir}/reports/jacoco/report/test/html")
            }
            dependsOn(jacocoTestReport)
        }

        register<JavaExec>("ktlintCheck") {
            inputs.files(project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt")))
            outputs.dir("${project.buildDir}/reports/ktlint/")

            group = "verification"
            description = "Check Kotlin code style."
            classpath = ktlint
            main = "com.pinterest.ktlint.Main"
            args = listOf("src/**/*.kt")
        }

        register<JavaExec>("ktlintFormat") {
            inputs.files(project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt")))
            outputs.dir("${project.buildDir}/reports/ktlint/")

            group = "verification"
            description = "Fix Kotlin code style deviations."
            classpath = ktlint
            main = "com.pinterest.ktlint.Main"
            args = listOf("-F", "src/**/*.kt")
        }
    }
}
