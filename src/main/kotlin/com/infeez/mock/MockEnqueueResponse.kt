package com.infeez.mock

import com.infeez.mock.utils.extractQueryParams
import okhttp3.mockwebserver.MockResponse

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    internal lateinit var mockData: MockData

    init {
        create(this)
    }

    fun doResponseWithUrl(url: String) {
        doResponseWithUrl(url) {
            emptyBody()
        }
    }

    fun doResponseWithUrl(url: String, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder()
        init(mockResponseBuilder)
        mockData = MockData(url, extractQueryParams(url), mockResponseBuilder.mockResponse)
    }
}

data class MockData(
    val url: String,
    val queryParams: Map<String, String>?,
    val mockResponse: MockResponse
)
