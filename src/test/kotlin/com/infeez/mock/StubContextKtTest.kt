package com.infeez.mock

import io.github.rybalkinsd.kohttp.dsl.httpGet
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StubContextKtTest {

    @Test
    fun stubContext() = createMockServer {
        mockScenario {
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
            host = hostName
            port = this@createMockServer.port
            path = "/base/mock/server"
        }

        assertTrue {
            response.body!!.string() == "response string"
        }

        assertTrue {
            response.headers["key"] == "value"
        }
    }

    @Test
    fun doubleResponseUseTest() = createMockServer {
        assertFailsWith<IllegalStateException>(message = "Please use only one way mocks dispatcher or enqueues") {
            mockScenario {
                add {
                    doResponseWithUrl("/one") {
                        fromString("")
                    }
                }
                add {
                    doResponse {
                        fromString("")
                    }
                }
            }
        }
    }

    private fun createMockServer(mockServer: MockWebServer.() -> Unit) {
        MockWebServer().run {
            start()
            mockServer(this)
            shutdown()
        }
    }
}