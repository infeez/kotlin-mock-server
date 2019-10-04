package com.infeez.mock

import okhttp3.mockwebserver.MockResponse

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    internal lateinit var mockResponse: MockResponse
    internal var url: String? = null

    init {
        create(this)
    }

    fun doResponse(init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder()
        init(mockResponseBuilder)
        mockResponse = mockResponseBuilder.mockResponse
    }

    fun doResponseWithUrl(url: String, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder()
        init(mockResponseBuilder)
        mockResponse = mockResponseBuilder.mockResponse
        this.url = url
    }
}