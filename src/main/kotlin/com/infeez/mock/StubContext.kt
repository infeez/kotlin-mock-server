package com.infeez.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class StubContext(private val mockWebServer: MockWebServer) {
    fun doResponse(init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder()
        init(mockResponseBuilder)
        mockWebServer.enqueue(mockResponseBuilder.mockResponse)
    }

    fun doResponseWithUrl(url: String, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder()
        init(mockResponseBuilder)
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.path == url) {
                    return mockResponseBuilder.mockResponse
                }
                return MockResponse().setResponseCode(404)
            }
        }
    }
}

fun MockWebServer.stubContext(init: StubContext.() -> Unit) {
    StubContext(this).apply(init)
}