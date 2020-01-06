package com.infeez.mock

import okhttp3.mockwebserver.MockResponse

class MockResponseHeaderBuilder(private val mockResponse: MockResponse) {
    infix fun String.withValue(value: Any) {
        mockResponse.addHeader(this, value)
    }
}
