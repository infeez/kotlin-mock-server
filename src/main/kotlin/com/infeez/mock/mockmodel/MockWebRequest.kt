package com.infeez.mock.mockmodel

data class MockWebRequest(
    val method: String,
    val path: String,
    val headers: Map<String, String>,
    val body: String? = null
)
