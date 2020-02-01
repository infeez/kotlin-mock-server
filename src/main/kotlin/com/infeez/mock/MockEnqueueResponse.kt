package com.infeez.mock

import com.infeez.mock.extensions.extractQueryParams
import com.infeez.mock.matcher.RequestMatcher
import okhttp3.mockwebserver.MockResponse

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    internal var url: String? = null
    internal var requestMatcher: RequestMatcher? = null
    internal var queryParams: Map<String, String>? = null
    lateinit var mockResponse: MockResponse

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

    inline fun <reified T> changeResponse(change: T.() -> Unit): MockEnqueueResponse {
        val body = mockResponse.getBody() ?: error("Body may not by null!")
        val data = body.inputStream().bufferedReader().use { it.readText() }
        val model = MockServerSettings.converterFactory!!.from<T>(data, T::class.java)
        change(model)
        mockResponse.setBody(MockServerSettings.converterFactory!!.to(model))
        return this
    }
}
