package com.infeez.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

private val responses = mutableMapOf<String, MockResponse>()

private val dispatcherDelegate = lazy {
    object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return responses[request.path] ?: MockResponse().setResponseCode(404)
        }
    }
}
private val dispatcher: Dispatcher by dispatcherDelegate

class ScenarioBuilder(private val mockWebServer: MockWebServer) {

    fun add(create: MockEnqueueResponse.() -> Unit) {
        add(MockEnqueueResponse(create))
    }

    fun add(response: MockEnqueueResponse) {
        checkAndSetDispatcher(response.url)
        if (response.url.isNullOrEmpty()) {
            mockWebServer.enqueue(response.mockResponse)
        } else {
            responses[response.url!!] = response.mockResponse
        }
    }

    fun addAll(responses: List<MockEnqueueResponse>) {
        responses.forEach { add(it) }
    }

    private fun checkAndSetDispatcher(url: String?) {
        if (!url.isNullOrEmpty()) {
            if (!dispatcherDelegate.isInitialized()) {
                mockWebServer.dispatcher = dispatcher
            } else if (mockWebServer.dispatcher != dispatcher) {
                mockWebServer.dispatcher = dispatcher
            }
        } else {
            check(!dispatcherDelegate.isInitialized()) {
                "Please use only one way mocks dispatcher or enqueues"
            }
        }
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