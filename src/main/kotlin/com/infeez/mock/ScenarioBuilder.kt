package com.infeez.mock

import com.infeez.mock.utils.extractQueryParams
import java.net.HttpURLConnection
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ScenarioBuilder(mockWebServer: MockWebServer) {

    private val responses = mutableMapOf<String, MockData>()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val mockData = responses[request.requestUrl?.encodedPath]

                val result = request.path?.let { extractQueryParams(it) } == mockData?.url?.let { extractQueryParams(it) }

                if (!result) {
                    return MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }

                return mockData?.mockResponse ?: MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }

    fun add(create: MockEnqueueResponse.() -> Unit) {
        add(MockEnqueueResponse(create))
    }

    fun add(response: MockEnqueueResponse) {
        responses[response.mockData.url] = response.mockData
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
