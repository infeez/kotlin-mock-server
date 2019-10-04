package com.infeez.mock

import io.github.rybalkinsd.kohttp.dsl.httpGet
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class StubContextKtTest {

    lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @Test
    fun stubContext() {
        mockWebServer.mockScenario {
            add {
                doResponseWithUrl("/base/mock/server") {
                    fromString("response string") {
                        responseStatusCode = 200
                        socketPolicy = SocketPolicy.CONTINUE_ALWAYS
                        headers {
                            "key" withValue "value"
                        }
                        bodyDelay {
                            delay = 100
                            unit = TimeUnit.MILLISECONDS
                        }
                        headersDelay {
                            delay = 100
                            unit = TimeUnit.MILLISECONDS
                        }
                    }
                }
            }
        }

        val response = httpGet {
            host = mockWebServer.hostName
            port = mockWebServer.port
            path = "/base/mock/server"
        }

        assertTrue {
            response.body!!.string() == "response string"
        }

        assertTrue {
            response.headers["key"] == "value"
        }
    }

    @After
    fun dispose() {
        mockWebServer.shutdown()
    }
}