package com.infeez.mock

import com.infeez.mock.utils.extractQueryParams
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URLDecoder
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ScenarioBuilder(mockWebServer: MockWebServer) {

    private val responsesWithUrl = mutableMapOf<String, MockEnqueueResponse>()
    private val responsesWithMatcher = mutableListOf<MockEnqueueResponse>()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                for (res in responsesWithMatcher) {
                    if (res.matcher != null && res.matcher?.matches(request) == true) {
                        return res.mockResponse
                    }
                }

                val decodedUrl = URLDecoder.decode(request.path, "utf-8")
                val urlWithParams = decodedUrl.split("?")
                val res = responsesWithUrl[urlWithParams.first()]

                if (urlWithParams.size == 2 && res?.queryParams != null && extractQueryParams(decodedUrl) == res.queryParams) {
                    return res.mockResponse
                }

                return res?.mockResponse ?: MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }

    fun add(create: MockEnqueueResponse.() -> Unit) {
        add(MockEnqueueResponse(create))
    }

    fun add(response: MockEnqueueResponse) {
        if (response.url == null && response.matcher == null) {
            throw IllegalStateException("Url or matcher not to be null")
        }
        if (response.url == null) {
            responsesWithMatcher.add(response)
        } else {
            responsesWithUrl[response.url!!] = response
        }
    }

    fun addAll(responses: List<MockEnqueueResponse>) {
        responses.forEach { add(it) }
    }
}

fun MockWebServer.mockScenario(create: ScenarioBuilder.() -> Unit) {
    create(ScenarioBuilder(this))
}

fun withMockServer(mockServer: MockWebServer.() -> Unit) {
    MockWebServer().run {
        start()
        mockServer(this)
        shutdown()
    }
}
