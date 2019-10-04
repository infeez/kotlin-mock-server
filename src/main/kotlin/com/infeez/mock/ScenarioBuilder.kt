package com.infeez.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.lang.IllegalStateException

class ScenarioBuilder(private val mockWebServer: MockWebServer) {
    private val responses = mutableListOf<Pair<String, MockResponse>>()

    private val dispatcherDelegate = lazy {
        object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return responses.find { request.path == it.first }?.second ?: MockResponse().setResponseCode(404)
            }
        }
    }
    private val dispatcher: Dispatcher by dispatcherDelegate


    fun add(create: MockEnqueueResponse.() -> Unit) {
        val a = MockEnqueueResponse(create)
        add(a)
    }

    fun add(response: MockEnqueueResponse) {
        checkAndSetDispatcher(response.url)
        response.url?.run {
            responses.add(this to response.mockResponse)
        } ?: run {
            mockWebServer.enqueue(response.mockResponse)
        }
    }

    fun addAll(responses: List<MockEnqueueResponse>) {
        responses.forEach { add(it) }
    }

    private fun checkAndSetDispatcher(url: String?) {
        if (!url.isNullOrEmpty()) {
            if (!dispatcherDelegate.isInitialized()) {
                mockWebServer.dispatcher = dispatcher
            }
        } else {
            if (dispatcherDelegate.isInitialized()) {
                throw IllegalStateException("Please use only one way mocks dispatcher or enqueues")
            }
        }
    }
}

fun MockWebServer.mockScenario(create: ScenarioBuilder.() -> Unit) {
    create(ScenarioBuilder(this))
}