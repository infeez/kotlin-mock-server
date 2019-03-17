package com.infeez.mock

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy

class MockResponseParameterBuilder(private val mockResponse: MockResponse) {

    var responseStatusCode: Int = 200
        set(value) {
            mockResponse.setResponseCode(value)
            field = value
        }

    var socketPolicy: SocketPolicy? = null
        set(value) {
            value?.run {
                mockResponse.setSocketPolicy(value)
            }
            field = value
        }

    fun headers(init: () -> Unit) {
        init()
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

    infix fun String.withValue(value: Any) {
        mockResponse.addHeader(this, value)
    }
}