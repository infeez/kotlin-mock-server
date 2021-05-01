package com.infeez.mock.server.impl

import com.infeez.mock.mockmodel.MockWebRequest
import com.infeez.mock.mockmodel.MockWebResponse
import com.infeez.mock.server.Configuration
import com.infeez.mock.server.Server
import java.net.InetAddress
import java.util.concurrent.TimeUnit.MILLISECONDS
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class OkHttpServer(configuration: Configuration) : Server(configuration) {

    private val mockWebServer = MockWebServer()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val body = if (request.bodySize > 0) {
                    request.body.clone().inputStream().use { i -> i.bufferedReader().use { it.readText() } }
                } else {
                    null
                }
                val mockWebRequest = MockWebRequest(request.method!!, request.path!!, request.headers.toMap(), body)

                return onDispatch.invoke(mockWebRequest).toMockResponse()
            }
        }
    }

    override fun start() {
        mockWebServer.start(InetAddress.getByName(configuration.host), configuration.port)
    }

    override fun stop() {
        mockWebServer.shutdown()
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
