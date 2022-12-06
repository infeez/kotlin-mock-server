package io.github.infeez.kotlinmockserver.mockmodel

data class MockWebRequest(
    val method: String,
    val path: String,
    val queries: Map<String, String>,
    val headers: Map<String, String>,
    val body: String? = null
)
