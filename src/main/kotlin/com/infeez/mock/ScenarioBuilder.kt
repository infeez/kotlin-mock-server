package com.infeez.mock

import com.infeez.mock.extensions.checkUrlParamWithAsterisk
import com.infeez.mock.extensions.decodeUrl
import com.infeez.mock.extensions.extractQueryParams
import io.github.rybalkinsd.kohttp.dsl.context.HttpContext
import io.github.rybalkinsd.kohttp.dsl.context.HttpPostContext
import io.github.rybalkinsd.kohttp.dsl.httpDelete
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpHead
import io.github.rybalkinsd.kohttp.dsl.httpPatch
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.dsl.httpPut
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URI
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ScenarioBuilder(mockWebServer: MockWebServer) {

    private var responsesWithUrl = mutableMapOf<String, MockEnqueueResponse>()
    private var responsesWithMatcher = mutableListOf<MockEnqueueResponse>()

    var mockServerBehavior: MockServerBehavior = MockServerBehavior.ErrorWhenMockNotFound

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

                return when (mockServerBehavior) {
                    MockServerBehavior.ErrorWhenMockNotFound -> {
                        MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                    }
                    MockServerBehavior.PassWhenMockNotFound -> {
                        failSafe(request)
                    }
                }
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

    private fun failSafe(request: RecordedRequest): MockResponse {
        val uri = URI(MockServerSettings.failSafeServerUrl)
        val initGet: HttpContext.() -> Unit = {
            this.host = uri.host
            this.port = uri.port
            this.path = request.path
            header { request.headers.toMultimap().map { it.key to it.value.first() } }
        }
        val initPost: HttpPostContext.() -> Unit = {
            this.host = uri.host
            this.port = uri.port
            this.path = request.path
            header { request.headers.toMultimap().map { it.key to it.value.first() } }
            body { bytes(request.body.readByteArray()) }
        }
        val response = when (request.method) {
            "POST" -> httpPost(init = initPost)
            "PUT" -> httpPut(init = initPost)
            "DELETE" -> httpDelete(init = initPost)
            "PATCH" -> httpPatch(init = initPost)
            "HEAD" -> httpHead(init = initGet)
            "GET" -> httpGet(init = initGet)
            else -> {
                error("Unknown http method type: ${request.method}")
            }
        }

        return MockResponse().apply {
            headers = response.headers
            response.body?.let { setBody(it.string()) }
            setResponseCode(response.code)
        }
    }
}

fun MockWebServer.mockScenario(create: ScenarioBuilder.() -> Unit): ScenarioBuilder {
    val scenarioBuilder = ScenarioBuilder(this)
    create(scenarioBuilder)
    return scenarioBuilder
}

fun withMockServer(mockServer: MockWebServer.() -> Unit) {
    MockWebServer().run {
        start()
        mockServer(this)
        shutdown()
    }
}
