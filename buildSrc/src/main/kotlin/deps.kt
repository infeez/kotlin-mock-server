object Versions {
    const val junit4 = "4.13"
    const val kotlin = "1.5.20"
    const val ktlint = "0.38.0"
    const val kohttp = "0.12.0"
    const val gson = "2.8.7"
    const val okhttp3MockWebServer = "4.9.1"
    const val mockitoKotlin = "3.2.0"
}

object Dependencies {
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
    const val junit4 = "junit:junit:${Versions.junit4}"
    const val kohttp = "io.github.rybalkinsd:kohttp:${Versions.kohttp}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val okhttp3MockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp3MockWebServer}"
}
