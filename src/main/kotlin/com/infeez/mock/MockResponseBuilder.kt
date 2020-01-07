package com.infeez.mock

import java.io.File
import java.io.InputStream
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy

class MockResponseBuilder {
    internal var mockResponse = MockResponse()

    var responseStatusCode: Int = 200
        set(value) {
            mockResponse.setResponseCode(value)
            field = value
        }

    var socketPolicy: SocketPolicy? = null
        set(value) {
            value?.run {
                mockResponse.apply {
                    socketPolicy = value
                }
            }
            field = value
        }

    fun headers(init: MockResponseHeaderBuilder.() -> Unit) {
        val headerBuilder = MockResponseHeaderBuilder(mockResponse)
        init(headerBuilder)
    }

    fun bodyDelay(init: MockResponseParameterDelayBuilder.() -> Unit) {
        val bodyDelayBuilder = MockResponseParameterDelayBuilder()
        init(bodyDelayBuilder)
        mockResponse.setBodyDelay(bodyDelayBuilder.delay, bodyDelayBuilder.unit)
    }

    fun headersDelay(init: MockResponseParameterDelayBuilder.() -> Unit) {
        val bodyDelayBuilder = MockResponseParameterDelayBuilder()
        init(bodyDelayBuilder)
        mockResponse.setHeadersDelay(bodyDelayBuilder.delay, bodyDelayBuilder.unit)
    }

    fun fromString(bodyString: String) {
        mockResponse.setBody(bodyString)
    }

    fun fromStream(inputStream: InputStream) {
        mockResponse.setBody(inputStream.use { it.reader().readText() })
    }

    fun fromFile(file: File) {
        fromStream(file.inputStream())
    }

    fun fromFile(filePath: String) {
        fromStream(File(filePath).inputStream())
    }

    fun emptyBody() {
        fromString("")
    }
}
