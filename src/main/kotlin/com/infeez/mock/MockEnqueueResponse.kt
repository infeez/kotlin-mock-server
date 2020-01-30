package com.infeez.mock

import com.infeez.mock.extensions.extractQueryParams
import com.infeez.mock.matcher.RequestMatcher
import okhttp3.mockwebserver.MockResponse

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    internal var url: String? = null
    internal var requestMatcher: RequestMatcher? = null
    internal var queryParams: Map<String, String>? = null
    internal lateinit var mockResponse: MockResponse

    init {
        create(this)
    }

    fun doResponseWithUrl(url: String) {
        doResponseWithUrl(url) {
            emptyBody()
        }
    }

    fun doResponseWithUrl(url: String, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder().apply(init)
        this.url = url.split("?").first().let { u -> u.takeUnless { it.startsWith("/") }?.let { "/$it" } ?: u }
        this.queryParams = url.extractQueryParams()
        this.mockResponse = mockResponseBuilder.mockResponse
    }

    fun doResponseWithMatcher(requestMatcher: RequestMatcher, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder().apply(init)
        this.requestMatcher = requestMatcher
        this.mockResponse = mockResponseBuilder.mockResponse
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MockEnqueueResponse

        if (url != other.url) return false
        if (queryParams != other.queryParams) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url?.hashCode() ?: 0
        result = 31 * result + (queryParams?.hashCode() ?: 0)
        return result
    }
}
