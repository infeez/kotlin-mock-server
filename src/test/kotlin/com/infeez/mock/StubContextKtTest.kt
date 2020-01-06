package com.infeez.mock

import io.github.rybalkinsd.kohttp.dsl.httpGet
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Test

class StubContextKtTest {

    @Test
    fun `mock test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/base/mock/server") {
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
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/base/mock/server"
        }

        assertEquals("response string", response.body!!.string())
        assertEquals("value", response.headers["key"])
    }

    @Test
    fun `double response use test`() = withMockServer {
        assertFailsWith<IllegalStateException>(message = "Please use only one way mocks dispatcher or enqueues") {
            mockScenario {
                add {
                    doResponseWithUrl("/one") {
                        fromString("")
                    }
                }
                add {
                }
            }
        }
    }

    @Test
    fun `some mock servers`() {
        val mockServer1 = MockWebServer()
        val mockServer2 = MockWebServer()
        mockServer1.start()
        mockServer2.start()

        mockServer1.mockScenario {
            add {
                doResponseWithUrl("/one") {
                    fromString("one")
                }
            }
        }

        mockServer2.mockScenario {
            add {
                doResponseWithUrl("/two") {
                    fromString("two")
                }
            }
        }

        val response1 = httpGet {
            host = mockServer1.hostName
            port = mockServer1.port
            path = "/one"
        }

        assertEquals("one", response1.body!!.string())

        val response2 = httpGet {
            host = mockServer2.hostName
            port = mockServer2.port
            path = "/two"
        }

        assertEquals("two", response2.body!!.string())

        mockServer1.shutdown()
        mockServer2.shutdown()
    }
}
