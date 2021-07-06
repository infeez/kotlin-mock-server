val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.41.0") {
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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
        classpath("com.android.tools.build:gradle:4.2.0") // IDEA issue with 4.2 - https://youtrack.jetbrains.com/issue/IDEA-268968
    }
}

plugins {
    id("org.jetbrains.dokka") version "1.4.32"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    jacoco
    java
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt.yml")
    reports {
        html.enabled = true
    }
}

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektFull") {
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
    apply(plugin = "jacoco")
    apply(plugin = "java")

    jacoco {
        toolVersion = "0.8.7"
    }

    tasks.withType<JacocoReport>() {
        reports {
            html.isEnabled = true
            xml.isEnabled = true
        }
    }

    tasks.register<JacocoReport>("jacocoFullReport") {
        subprojects {
            val subproject = this
            subproject.plugins.withType<JacocoPlugin>().configureEach {
                subproject.tasks.matching { it.extensions.findByType<JacocoTaskExtension>() != null }.configureEach {
                    val testTask = this
                    try {
                        sourceSets(subproject.the<SourceSetContainer>()["main"])
                        executionData(testTask)
                    } catch (e: Throwable){}
                }
            }
        }

        reports {
            xml.isEnabled = true
            xml.destination = File("${buildDir}/reports/jacoco/report/test/jacocoTestReport.xml")
            html.isEnabled = true
            html.destination = File("${buildDir}/reports/jacoco/report/test/html")
        }
    }

    val outputDir = "${project.buildDir}/reports/ktlint/"
    val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

    tasks.register<JavaExec>("ktlintCheck") {
        inputs.files(inputFiles)
        outputs.dir(outputDir)

        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args = listOf("src/**/*.kt")
    }

    tasks.register<JavaExec>("ktlintFormat") {
        inputs.files(inputFiles)
        outputs.dir(outputDir)

        group = "verification"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args = listOf("-F", "src/**/*.kt")
    }

    repositories {
        google()
        mavenCentral()
    }
}
