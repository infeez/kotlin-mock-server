package com.infeez.mock

import com.infeez.mock.matcher.Matcher
import com.infeez.mock.utils.extractQueryParams
import java.net.URLDecoder
import okhttp3.mockwebserver.MockResponse

class MockEnqueueResponse(create: MockEnqueueResponse.() -> Unit) {

    internal var url: String? = null
    internal var matcher: Matcher? = null
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
        val decodedUrl = URLDecoder.decode(url, "utf-8")
        this.url = url.split("?").first()
        this.queryParams = extractQueryParams(decodedUrl)
        this.mockResponse = mockResponseBuilder.mockResponse
    }

    fun doResponseWithUrl(matcher: Matcher, init: MockResponseBuilder.() -> Unit) {
        val mockResponseBuilder = MockResponseBuilder().apply(init)
        this.matcher = matcher
        this.mockResponse = mockResponseBuilder.mockResponse
    }
}
