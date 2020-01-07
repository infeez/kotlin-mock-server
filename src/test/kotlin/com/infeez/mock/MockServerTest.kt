package com.infeez.mock

import com.infeez.mock.matcher.and
import com.infeez.mock.matcher.endsWith
import com.infeez.mock.matcher.eq
import com.infeez.mock.matcher.or
import com.infeez.mock.matcher.ruleParam
import com.infeez.mock.matcher.rulePath
import com.infeez.mock.matcher.startWith
import io.github.rybalkinsd.kohttp.dsl.httpGet
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Test

class MockServerTest {

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
    fun `some mocks in one add block test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/one")
                doResponseWithUrl("/two")
                doResponseWithUrl("/three")
            }
        }

        var response = httpGet {
            host = this@withMockServer.hostName
            port = this@withMockServer.port
            path = "/one"
        }

        assertEquals("", response.body!!.string())

        response = httpGet {
            host = this@withMockServer.hostName
            port = this@withMockServer.port
            path = "/two"
        }

        assertEquals("", response.body!!.string())

        response = httpGet {
            host = this@withMockServer.hostName
            port = this@withMockServer.port
            path = "/three"
        }

        assertEquals("", response.body!!.string())
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

    @Test
    fun `query test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/mock/url?param1=1&param2=2") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param1=1&param2=2"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `reverse query test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/mock/url?param1=1&param3=a&param2=2") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param2=2&param1=1&param3=a"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path eq matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(rulePath eq "/mock/url") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path startWith matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(rulePath startWith "/mock") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path endsWith matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(rulePath endsWith "url") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `param eq matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(ruleParam("param") eq "1") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=1"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `param startWith matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(ruleParam("param") startWith "1") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=11111"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `param endsWith matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher(ruleParam("param") endsWith "21") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=222221"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path eq and param eq matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher((rulePath eq "/mock/url") and (ruleParam("param") eq "1")) {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=1"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path eq(true) or param eq(false) matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher((rulePath eq "/mock/url") or (ruleParam("param") eq "2")) {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=1"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path eq(false) or param eq(true) matcher test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher((rulePath eq "/some/path") or (ruleParam("param") eq "1")) {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param=1"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `slash out in url`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("url/without/first/slash") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/url/without/first/slash"
        }

        assertEquals("response string", response.body!!.string())
    }
}
