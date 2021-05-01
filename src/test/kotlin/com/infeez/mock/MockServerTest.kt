package com.infeez.mock

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.infeez.mock.converter.ConverterFactory
import com.infeez.mock.matcher.and
import com.infeez.mock.matcher.endsWith
import com.infeez.mock.matcher.eq
import com.infeez.mock.matcher.matchWithBody
import com.infeez.mock.matcher.or
import com.infeez.mock.matcher.ruleBody
import com.infeez.mock.matcher.ruleParam
import com.infeez.mock.matcher.rulePath
import com.infeez.mock.matcher.startWith
import com.infeez.mock.matcher.withConverter
import com.infeez.mock.matcher.withString
import com.infeez.mock.util.RequestMethod
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.dsl.httpPut
import io.github.rybalkinsd.kohttp.util.json
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import org.junit.Test

class MockServerTest {

    private val gsonConverterFactory = object : ConverterFactory {
        private val gson = Gson()
        override fun <T> from(value: String, type: Type): T {
            return gson.fromJson(value, type)
        }

        override fun <T> to(value: T): String {
            return gson.toJson(value)
        }
    }

    @Before
    fun setUp() {
        MockServerSettings.converterFactory = gsonConverterFactory
    }

    @After
    fun after() {
        MockServerSettings.failSafeServerUrl = ""
    }

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
    fun `query empty value param test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/mock/url?param1=1&param2=2&param3=") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/url?param1=1&param2=2&param3="
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
    fun `path param asterisk and query test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/mock/*/url?param1=1&param2=2") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/asterisk/url?param1=1&param2=2"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `path param asterisk last and query test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithUrl("/mock/*") {
                    fromString("response string")
                }
            }
        }

        val response = httpGet {
            host = hostName
            port = this@withMockServer.port
            path = "/mock/123"
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

    @Test
    fun `body param string test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody withString { this == "body string" })) {
                    fromString("response string")
                }
            }
        }

        val response = httpPost {
            host = hostName
            body {
                string("body string")
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `body param converter full json test`() = withMockServer {
        mockScenario {

            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "a" && b == 1 && c == 2L && d == 3.0
                })) {
                    fromString("response string")
                }
            }
        }

        val response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `body param converter one field json test`() = withMockServer {
        mockScenario {

            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "a"
                })) {
                    fromString("response string")
                }
            }
        }

        val response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string", response.body!!.string())
    }

    @Test
    fun `body param converter no one field json test`() = withMockServer {
        mockScenario {

            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "aa"
                })) {
                    fromString("response string")
                }
            }
        }

        val response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertNotEquals("response string", response.body!!.string())
    }

    @Test
    fun `replace mock test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString("response string1")
            }
        }
        val mock2 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString("response string2")
            }
        }

        val mockScenario = mockScenario {
            addAll(mock1, mock2)
        }

        var response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string1", response.body!!.string())

        mockScenario.replace(mock1, mock2)

        response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string2", response.body!!.string())
    }

    @Test
    fun `remove mock test`() = withMockServer {
        val mock = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString("response string")
            }
        }

        val mockScenario = mockScenario {
            add(mock)
        }

        var response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string", response.body!!.string())

        mockScenario.remove(mock)

        response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertNotEquals("response string", response.body!!.string())
    }

    @Test
    fun `change mock test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString(json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                })
            }
        }

        val mockScenario = mockScenario {
            add(mock1)
        }

        var response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body!!.string())

        val mock2 = mock1.changeResponse<StubModel> {
            d = 55.5
        }

        mockScenario.replace(mock1, mock2)

        response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body!!.string())
    }

    @Test
    fun `copyResponse mock test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                responseStatusCode = 201
                headers {
                    "a" withValue "123"
                }
                bodyDelay {
                    delay = 100
                }
                headersDelay {
                    delay = 200
                }
                fromString(json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                })
            }
        }

        val mock2 = mock1.copyResponse<StubModel> {
            d = 55.5
        }

        mockScenario {
            add(mock2)
        }

        val response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals(mock2.mockResponseBuilder.bodyDelayBuilder!!.delay, 100)
        assertEquals(mock2.mockResponseBuilder.headerDelayBuilder!!.delay, 200)

        assertEquals("123", response.headers["a"])
        assertEquals(201, response.code)
        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body!!.string())
    }

    @Test
    fun `copyResponse mock not affect copied mock test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString(json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                })
            }
        }

        mockScenario {
            add(mock1)
        }

        mock1.copyResponse<StubModel> {
            d = 55.5
        }

        val response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body!!.string())
    }

    @Test
    fun `replace mock response test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                responseStatusCode = 201
                headers {
                    "a" withValue "123"
                }
                bodyDelay {
                    delay = 100
                }
                headersDelay {
                    delay = 200
                }
                fromString(json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                })
            }
        }

        val scenario = mockScenario {
            add(mock1)
        }

        val mock2 = scenario.replaceMockResponse<StubModel>(mock1) {
            d = 55.5
        }

        val response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals(mock2.mockResponseBuilder.bodyDelayBuilder!!.delay, 100)
        assertEquals(mock2.mockResponseBuilder.headerDelayBuilder!!.delay, 200)

        assertEquals("123", response.headers["a"])
        assertEquals(201, response.code)
        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body!!.string())
    }

    @Test
    fun `body param converter one url multiple time test`() = withMockServer {
        mockScenario {
            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "a"
                })) {
                    fromString("response string a")
                }
            }
            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "b"
                })) {
                    fromString("response string b")
                }
            }
            add {
                doResponseWithMatcher((rulePath eq "/some/path") and (ruleBody.withConverter<StubModel> {
                    a == "c"
                })) {
                    fromString("response string c")
                }
            }
        }

        var response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string a", response.body!!.string())

        response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "b"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string b", response.body!!.string())

        response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "c"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string c", response.body!!.string())
    }

    @Test
    fun `change generic mock test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher(rulePath eq "/some/path") {
                fromString("""{"items":[{"a":"a","b":1,"c":2,"d":3.0}]}""")
            }
        }

        val scenario = mockScenario {
            add(mock1)
        }

        scenario.replaceMockResponse<ListInfo<StubModel>>(object : TypeToken<ListInfo<StubModel>>() {}.type, mock1) {
            items[0].apply {
                a = "b"
                b = 2
                c = 3
                d = 4.0
            }
        }

        val response = httpPost {
            host = hostName
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("""{"items":[{"a":"b","b":2,"c":3,"d":4.0}]}""", response.body!!.string())
    }

    @Test
    fun `matchWithBody test`() = withMockServer {
        val mock1 = MockEnqueueResponse {
            doResponseWithMatcher((rulePath eq "/some/path") and ruleBody.matchWithBody<StubModel>("""{"a":"a","b":1,"c":2,"d":3.0}""")) {
                fromString("response string a")
            }
        }

        mockScenario {
            add(mock1)
        }

        val response = httpPost {
            host = hostName
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            port = this@withMockServer.port
            path = "/some/path"
        }

        assertEquals("response string a", response.body!!.string())
    }

    @Test
    fun `some mock type test`() {
        val mockServer1 = MockWebServer()
        val mockServer2 = MockWebServer()
        mockServer1.start()
        mockServer2.start()

        mockServer1.mockScenario {
            add {
                doResponseWithUrl(RequestMethod.POST, "/one") {
                    fromString("one")
                }
            }
        }

        mockServer2.mockScenario {
            add {
                doResponseWithUrl(RequestMethod.PUT, "/two") {
                    fromString("two")
                }
            }
        }

        val response1 = httpPost {
            host = mockServer1.hostName
            port = mockServer1.port
            path = "/one"
        }

        assertEquals("one", response1.body!!.string())

        val response2 = httpPut {
            host = mockServer2.hostName
            port = mockServer2.port
            path = "/two"
        }

        assertEquals("two", response2.body!!.string())

        mockServer1.shutdown()
        mockServer2.shutdown()
    }

    @Test
    fun `double read body test`() = withMockServer {
        mockScenario {
            add(MockEnqueueResponse {
                doResponseWithMatcher(rulePath eq "/some/path") {
                    fromString("response string a")
                }
            })
        }

        httpPost {
            host = this@withMockServer.hostName
            port = this@withMockServer.port
            path = "/some/path"
            body {
                string("request string a")
            }
        }

        assertEquals("request string a", takeRequest().body.readUtf8())
    }

    data class ListInfo<T>(
        val items: List<T>
    )

    data class StubModel(
        var a: String,
        var b: Int,
        var c: Long,
        var d: Double
    )
}
