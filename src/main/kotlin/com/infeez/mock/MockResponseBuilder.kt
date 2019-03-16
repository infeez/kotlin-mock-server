package com.infeez.mock

import okhttp3.mockwebserver.MockResponse
import java.io.InputStream

class MockResponseBuilder {
    internal var mockResponse = MockResponse()

    fun fromStream(inputStream: InputStream, init: MockResponseParameterBuilder.() -> Unit) {
        mockResponse.setBody(inputStream.use { it.reader().readText() })
        init(MockResponseParameterBuilder(mockResponse))
    }

    fun fromString(bodyString: String, init: MockResponseParameterBuilder.() -> Unit) {
        mockResponse.setBody(bodyString)
        init(MockResponseParameterBuilder(mockResponse))
    }

    fun fromStream(inputStream: InputStream) {
        mockResponse.setBody(inputStream.use { it.reader().readText() })
    }

    fun fromString(bodyString: String) {
        mockResponse.setBody(bodyString)
    }
}