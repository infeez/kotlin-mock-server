package com.infeez.mock

import okhttp3.mockwebserver.MockResponse

class MockResponseParameterBuilder(private val mockResponse: MockResponse) {

    fun withStatusCode(statusCode: Int) {
        mockResponse.setResponseCode(statusCode)
    }

    fun withHeaders(init: () -> Unit) {
        init()
    }

    infix fun String.withValue(value: Any) {
        mockResponse.addHeader(this, value)
    }
}