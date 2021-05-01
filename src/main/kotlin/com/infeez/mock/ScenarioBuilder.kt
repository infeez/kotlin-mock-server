package com.infeez.mock

import com.infeez.mock.extensions.checkUrlParamWithAsterisk
import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.extensions.extractQueryParams
import com.infeez.mock.util.RequestMethod
import java.lang.reflect.Type
import java.net.HttpURLConnection
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

@Deprecated(
    message = "Please use new API for mock. See more in documentation.",
    replaceWith = ReplaceWith("MockServerContext(server, settings)", "com.infeez.mock.dsl.context")
)
class ScenarioBuilder(mockWebServer: MockWebServer) {

    private var responsesWithUrl = mutableMapOf<String, MockEnqueueResponse>()
    private var responsesWithMatcher = mutableListOf<MockEnqueueResponse>()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val decodedUrl = request.path!!.decodeUrl()
                val urlWithParams = decodedUrl.split("?")
                val url = urlWithParams.first()
                val method = request.method

                val resWithUrl = if (responsesWithUrl.keys.count { it.contains("*") } > 0) {
                    responsesWithUrl.filter { it.key.checkUrlParamWithAsterisk(url) }.map { it.value }.first()
                } else {
                    responsesWithUrl[url]
                }

                if (checkRequestMethod(resWithUrl?.requestMethod, method) && urlWithParams.size == 2 && resWithUrl?.queryParams != null && decodedUrl.extractQueryParams() == resWithUrl.queryParams) {
                    return resWithUrl.mockResponseBuilder.mockResponse
                }

                if (checkRequestMethod(resWithUrl?.requestMethod, method) && resWithUrl?.mockResponseBuilder?.mockResponse != null) {
                    return resWithUrl.mockResponseBuilder.mockResponse
                }

                val path = request.path
                val body = request.body.clone().inputStream().bufferedReader().use { it.readText() }
                for (res in responsesWithMatcher) {
                    if (checkRequestMethod(res.requestMethod, method) && res.requestMatcher != null && res.requestMatcher?.invoke(path, body) == true) {
                        return res.mockResponseBuilder.mockResponse
                    }
                }

                return MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }

    fun add(create: MockEnqueueResponse.() -> Unit) {
        add(MockEnqueueResponse(create))
    }

    fun add(response: MockEnqueueResponse) {
        if (response.url == null && response.requestMatcher == null) {
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

    fun addAll(vararg responses: MockEnqueueResponse) {
        responses.forEach { add(it) }
    }

    fun replace(from: MockEnqueueResponse, to: MockEnqueueResponse) {
        responsesWithUrl = responsesWithUrl.map { if (it.value == from) it.key to to else it.key to it.value }.toMap().toMutableMap()
        val index = responsesWithMatcher.indexOf(from)
        if (index >= 0) {
            responsesWithMatcher[index] = to
        }
    }

    inline fun <reified T> replaceMockResponse(from: MockEnqueueResponse, change: T.() -> Unit): MockEnqueueResponse {
        val replaced = from.copyResponse(change)
        replace(from, replaced)
        return replaced
    }

    inline fun <T> replaceMockResponse(type: Type, from: MockEnqueueResponse, change: T.() -> Unit): MockEnqueueResponse {
        val replaced = from.copyResponse(type, change)
        replace(from, replaced)
        return replaced
    }

    fun remove(response: MockEnqueueResponse) {
        responsesWithUrl = responsesWithUrl.filterValues { it != response }.toMutableMap()
        responsesWithMatcher.removeAll { it == response }
    }

    private fun checkRequestMethod(src: RequestMethod?, trg: String?): Boolean {
        if (src == null || src == RequestMethod.ANY) {
            return true
        }

        return src.method == trg
    }
}

@Deprecated(
    message = "Please use new API for mock. See more in documentation.",
    replaceWith = ReplaceWith("customMockServer(server, context, settings)", "com.infeez.mock.dsl")
)
fun MockWebServer.mockScenario(create: ScenarioBuilder.() -> Unit): ScenarioBuilder {
    val scenarioBuilder = ScenarioBuilder(this)
    create(scenarioBuilder)
    return scenarioBuilder
}

@Deprecated(
    message = "Please use new API for mock. See more in documentation.",
    replaceWith = ReplaceWith("customMockServer(server, context, settings)", "com.infeez.mock.dsl")
)
fun withMockServer(mockServer: MockWebServer.() -> Unit) {
    MockWebServer().run {
        start()
        mockServer(this)
        shutdown()
    }
}
