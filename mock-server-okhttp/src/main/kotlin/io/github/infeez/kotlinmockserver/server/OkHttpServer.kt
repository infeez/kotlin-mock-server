package io.github.infeez.kotlinmockserver.server

import io.github.infeez.kotlinmockserver.extensions.decodeUrl
import io.github.infeez.kotlinmockserver.extensions.extractQueryParams
import io.github.infeez.kotlinmockserver.mockmodel.MockWebRequest
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import java.net.InetAddress
import java.util.concurrent.TimeUnit.MILLISECONDS
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class OkHttpServer(
    serverConfiguration: ServerConfiguration
) : Server(serverConfiguration) {

    private val mockWebServer = MockWebServer()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val body = if (request.bodySize > 0) {
                    request.body.clone().inputStream().use { i -> i.bufferedReader().use { it.readText() } }
                } else {
                    null
                }
                val mockWebRequest = MockWebRequest(
                    method = request.method!!,
                    path = request.path!!,
                    queries = request.path!!.decodeUrl().extractQueryParams(),
                    headers = request.headers.toMap(),
                    body = body
                )

                return onDispatch.invoke(mockWebRequest).toMockResponse()
            }
        }
    }

    override fun start() {
        mockWebServer.start(InetAddress.getByName(serverConfiguration.host), serverConfiguration.port)
    }

    override fun stop() {
        mockWebServer.shutdown()
    }

    override fun getUrl(): String {
        return mockWebServer.url("/").toString()
    }

    private fun MockWebResponse.toMockResponse(): MockResponse {
        return MockResponse().apply {
            setBodyDelay(mockWebResponseParams.delay, MILLISECONDS)
            setHeadersDelay(mockWebResponseParams.delay, MILLISECONDS)
            this@toMockResponse.headers.takeIf { it.isNotEmpty() }?.let { headers = it.toHeaders() }
            body?.let { setBody(it) }
            setResponseCode(code)
        }
    }
}
