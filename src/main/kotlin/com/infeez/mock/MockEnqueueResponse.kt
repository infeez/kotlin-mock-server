package com.infeez.mock

import com.infeez.mock.extensions.extractQueryParams
import com.infeez.mock.matcher.RequestMatcher

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    var url: String? = null
    var requestMatcher: RequestMatcher? = null
    var queryParams: Map<String, String>? = null
    lateinit var mockResponseBuilder: MockResponseBuilder

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
        this.mockResponseBuilder = mockResponseBuilder
    }

    fun doResponseWithMatcher(requestMatcher: RequestMatcher, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder().apply(init)
        this.requestMatcher = requestMatcher
        this.mockResponseBuilder = mockResponseBuilder
    }

    inline fun <reified T> changeResponse(change: T.() -> Unit): MockEnqueueResponse {
        val body = mockResponseBuilder.mockResponse.getBody() ?: error("Body may not by null!")
        val data = body.inputStream().bufferedReader().use { it.readText() }
        val model = MockServerSettings.converterFactory!!.from<T>(data, T::class.java)
        change(model)
        mockResponseBuilder.mockResponse.setBody(MockServerSettings.converterFactory!!.to(model))
        return this
    }

    inline fun <reified T> copyResponse(change: T.() -> Unit): MockEnqueueResponse {
        val body = mockResponseBuilder.mockResponse.getBody() ?: error("Body may not by null!")
        val data = body.inputStream().bufferedReader().use { it.readText() }
        val model = MockServerSettings.converterFactory!!.from<T>(data, T::class.java)
        change(model)
        return MockEnqueueResponse {}.apply {
            this.url = this@MockEnqueueResponse.url
            this.requestMatcher = this@MockEnqueueResponse.requestMatcher
            this.queryParams = this@MockEnqueueResponse.queryParams
            this.mockResponseBuilder = MockResponseBuilder().apply {
                responseStatusCode = this@MockEnqueueResponse.mockResponseBuilder.responseStatusCode
                socketPolicy = this@MockEnqueueResponse.mockResponseBuilder.socketPolicy
                this@MockEnqueueResponse.mockResponseBuilder.mockResponse.headers.forEach {
                    mockResponse.addHeader(it.first, it.second)
                }
                this@MockEnqueueResponse.mockResponseBuilder.bodyDelayBuilder?.let {
                    bodyDelayBuilder = MockResponseParameterDelayBuilder().apply {
                        delay = it.delay
                        unit = it.unit
                    }
                }
                this@MockEnqueueResponse.mockResponseBuilder.headerDelayBuilder?.let {
                    headerDelayBuilder = MockResponseParameterDelayBuilder().apply {
                        delay = it.delay
                        unit = it.unit
                    }
                }

                fromString(MockServerSettings.converterFactory!!.to(model))
            }
        }
    }
}
