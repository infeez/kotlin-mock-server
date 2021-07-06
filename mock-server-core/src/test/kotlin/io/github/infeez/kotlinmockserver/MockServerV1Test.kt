package io.github.infeez.kotlinmockserver

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.infeez.kotlinmockserver.converter.ConverterFactory
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.dsl.http.mock
import io.github.infeez.kotlinmockserver.extensions.changeMockBody
import io.github.infeez.kotlinmockserver.extensions.copy
import io.github.infeez.kotlinmockserver.extensions.mock
import io.github.infeez.kotlinmockserver.matcher.and
import io.github.infeez.kotlinmockserver.matcher.or
import io.github.infeez.kotlinmockserver.mockmodel.MockWebRequest
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import io.github.infeez.kotlinmockserver.util.RequestMethod
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import org.junit.Test

class MockServerV1Test {

    private val defaultResponse = "response string"

    private val gsonConverterFactory = object : ConverterFactory {
        private val gson = Gson()
        override fun <T> from(value: String, type: Type): T {
            return gson.fromJson(value, type)
        }

        override fun <T> to(value: T): String {
            return gson.toJson(value)
        }
    }

    private val server = TestServer()

    @Test
    fun `mock test`() = runServer {
        mock("/base/mock/server") {
            headers("key" to "value")
            body(defaultResponse)
        }

        get("/base/mock/server").also {
            assertEquals(defaultResponse, it.body)
            assertEquals("value", it.headers["key"])
        }
    }

    @Test
    fun `url mock query test`() = runServer {
        mock("/base/mock/server?param1=1&param2=2") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=2")
    }

    @Test
    fun `query empty value param test`() = runServer {
        mock("/base/mock/server?param1=1&param2=") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=")
    }

    @Test
    fun `reverse query test`() = runServer {
        mock("/base/mock/server?param2=2&param1=1") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=2")
    }

    @Test
    fun `path param asterisk and query test`() = runServer {
        mock("/mock/*/url?param1=1&param2=2") {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/asterisk/url?param1=1&param2=2")
    }

    @Test
    fun `path param asterisk last and query test`() = runServer {
        mock("/mock/*") {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/132")
    }

    @Test
    fun `header eq matcher test`() = runServer {
        mock {
            header("name") { eq("value") }
        } on {
            body(defaultResponse)
        }

        get(path = "/url/", headers = mapOf("name" to "value")).also {
            assertEquals(defaultResponse, it.body)
        }
    }

    @Test
    fun `path eq matcher test`() = runServer {
        mock {
            path { eq("/mock/url") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `path startWith matcher test`() = runServer {
        mock {
            path { startWith("/mock") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `path endsWith matcher test`() = runServer {
        mock {
            path { endsWith("/url") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `param eq matcher test`() = runServer {
        mock {
            query("param") { eq("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `param startWith matcher test`() = runServer {
        mock {
            query("param") { startWith("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1234")
    }

    @Test
    fun `param endsWith matcher test`() = runServer {
        mock {
            query("param") { endsWith("4") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1234")
    }

    @Test
    fun `path eq and param eq matcher test`() = runServer {
        mock {
            path { eq("/mock/url") } and query("param") { eq("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `path eq(true) or param eq(false) matcher test`() = runServer {
        mock {
            path { eq("/mock/url") } or query("param") { eq("2") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `path eq(false) or param eq(true) matcher test`() = runServer {
        mock {
            path { eq("/some/path") } or query("param") { eq("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `slash out in url`() = runServer {
        mock("url/without/first/slash") {
            body(defaultResponse)
        }

        getResultDefaultTest("/url/without/first/slash")
    }

    @Test
    fun `body param string test`() = runServer {
        mock {
            path { eq("/mock/url") } and body { eq("request body string") }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url", "request body string")
    }

    @Test
    fun `body param converter full json test`() = runServer {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "a" && b == 1 && c == 2L && d == 3.0 }
            }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url", """{ "a":"a","b":1,"c":2,"d":3.0 }""")
    }

    @Test
    fun `body param converter one field json test`() = runServer {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "a" }
            }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url", """{ "a":"a","b":1,"c":2,"d":3.0 }""")
    }

    @Test
    fun `body param converter no one field json test`() = runServer {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "aabb" }
            }
        } on {
            body(defaultResponse)
        }

        post(path = "/mock/url", body = """{ "a":"a","b":1,"c":2,"d":3.0 }""").also {
            assertNotEquals("response string", it.body)
        }
    }

    @Test
    fun `replace mock test`() {
        val mock1 = mock { path { eq("/mock/url") } } on {
            body("$defaultResponse 1")
        }

        val mock2 = mock { path { eq("/mock/url") } } on {
            body("$defaultResponse 2")
        }

        runServer {
            addAll(mock1, mock2)

            var response = post(path = "/mock/url")

            assertEquals("$defaultResponse 1", response.body)

            replace(mock1, mock2)

            response = post(path = "/mock/url")

            assertEquals("$defaultResponse 2", response.body)
        }
    }

    @Test
    fun `remove mock test`() {
        val mock = mock { path { eq("/mock/url") } } on {
            body(defaultResponse)
        }

        runServer {
            add(mock)

            var response = post(path = "/mock/url")

            assertEquals(defaultResponse, response.body)

            remove(mock)

            response = post(path = "/mock/url")

            assertNotEquals(defaultResponse, response.body)
        }
    }

    @Test
    fun `change mock response body test`() = runServer {
        val mock = io.github.infeez.kotlinmockserver.dsl.http.mock { path { eq("/mock/url") } } on {
            body("""{"a":"a","b":1,"c":2,"d":3.0}""")
        }

        add(mock)

        var response = post(path = "/mock/url")

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body)

        changeMockBody<StubModel>(mock) {
            d = 55.5
        }

        response = post(path = "/mock/url")

        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body)
    }

    @Test
    fun `change mock response body with generic test`() = runServer {
        val mock = io.github.infeez.kotlinmockserver.dsl.http.mock { path { eq("/mock/url") } } on {
            body("""{"items":[{"a":"a","b":1,"c":2,"d":3.0}]}""")
        }

        add(mock)

        changeMockBody<StubListInfo<StubModel>>(object : TypeToken<StubListInfo<StubModel>>() {}.type, mock) {
            items[0].apply {
                a = "b"
                b = 2
                c = 3
                d = 4.0
            }
        }

        post(path = "/mock/url").also {
            assertEquals("""{"items":[{"a":"b","b":2,"c":3,"d":4.0}]}""", it.body)
        }
    }

    @Test
    fun `copy mock test`() = runServer {
        val mock1 = io.github.infeez.kotlinmockserver.dsl.http.mock { path { eq("/mock/url") } } on {
            code(201)
            headers {
                "a" to "123"
            }
            delay(100, MILLISECONDS)
            body("""{"a":"a","b":1,"c":2,"d":3.0}""")
        }

        val mock2 = mock1.copy {
            body("""{"a":"a","b":1,"c":2,"d":55.5}""")
        }

        add(mock2)

        val response = post(path = "/mock/url")

        assertEquals(100, mock2.mockWebResponse.mockWebResponseParams.delay)

        assertEquals("123", response.headers["a"])
        assertEquals(201, response.code)
        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body)
    }

    @Test
    fun `copyResponse mock not affect copied mock test`() = runServer {
        val mock1 = io.github.infeez.kotlinmockserver.dsl.http.mock { path { eq("/mock/url") } } on {
            code(201)
            headers {
                "a" to "123"
            }
            delay(100, MILLISECONDS)
            body("""{"a":"a","b":1,"c":2,"d":3.0}""")
        }

        add(mock1)

        mock1.copy {
            body("""{"a":"a","b":121231,"c":2,"d":55.5}""")
        }

        val response = post(path = "/mock/url")

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body)
    }

    @Test
    fun `body param converter one url multiple time test`() = runServer {
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "a" } } } on {
            body(("response string a"))
        }
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "b" } } } on {
            body(("response string b"))
        }
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "c" } } } on {
            body(("response string c"))
        }

        var response = post(path = "/some/path", body = """{ "a":"a","b":1,"c":2,"d":3.0 }""")

        assertEquals("response string a", response.body)

        response = post(path = "/some/path", body = """{ "a":"b","b":1,"c":2,"d":3.0 }""")

        assertEquals("response string b", response.body!!)

        response = post(path = "/some/path", body = """{ "a":"c","b":1,"c":2,"d":3.0 }""")

        assertEquals("response string c", response.body!!)
    }

    @Test
    fun `bodyEq test`() = runServer {
        mock { path { eq("/some/path") } and body { bodyEq<StubModel>("""{"a":"a","b":1,"c":2,"d":3.0}""") } } on {
            body(("response string a"))
        }

        val response = post(path = "/some/path", body = """{ "a":"a","b":1,"c":2,"d":3.0 }""")

        assertEquals("response string a", response.body!!)
    }

    @Test
    fun `isNullOrEmpty null body test`() = runServer {
        mock { path { eq("/some/path") } and body { isNullOrEmpty() } } on {
            body(("response string a"))
        }

        val response = post(path = "/some/path", body = null)

        assertEquals("response string a", response.body!!)
    }

    @Test
    fun `isNullOrEmpty empty body test`() = runServer {
        mock { path { eq("/some/path") } and body { isNullOrEmpty() } } on {
            body(("response string a"))
        }

        val response = post(path = "/some/path", body = "")

        assertEquals("response string a", response.body!!)
    }

    @Test
    fun `isNullOrEmpty not null or empty body test`() = runServer {
        mock { path { eq("/some/path") } and body { isNullOrEmpty() } } on {
            body(("response string a"))
        }

        val response = post(path = "/some/path", body = "value")

        assertNull(response.body)
    }

    // тест тут не нужен
    @Test
    fun `double read body test`() = runServer {
        val mock = mock { path { eq("/some/path") } } on {
            body(defaultResponse)
        }

        post(path = "/some/path", body = "request string a")

        assertEquals("request string a", getRequestByMock(mock)!!.body)
    }

    private fun getResultDefaultTest(url: String) {
        getResultTest(url) {
            assertEquals(defaultResponse, body)
        }
    }

    private fun postResultDefaultTest(url: String, bodyStr: String? = null) {
        postResultTest(url, bodyStr) {
            assertEquals(defaultResponse, body)
        }
    }

    private fun getResultTest(url: String, result: MockWebResponse.() -> Unit) {
        result(get(path = url))
    }

    private fun postResultTest(url: String, bodyStr: String? = null, result: MockWebResponse.() -> Unit) {
        result(post(path = url, body = bodyStr))
    }

    private fun runServer(block: MockServerContext.() -> Unit) {
        server.start()
        block(
            MockServerContext(server) {
                converterFactory = gsonConverterFactory
            }
        )
        server.stop()
    }

    private fun request(
        method: RequestMethod,
        path: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = ""
    ): MockWebResponse {
        return server.request(
            MockWebRequest(
                method = method.method,
                path = path,
                headers = headers,
                body = body
            )
        )
    }

    private fun get(path: String, headers: Map<String, String> = emptyMap(), body: String = ""): MockWebResponse {
        return request(
            method = RequestMethod.GET,
            path = path,
            headers = headers,
            body = body
        )
    }

    private fun post(path: String, headers: Map<String, String> = emptyMap(), body: String? = ""): MockWebResponse {
        return request(
            method = RequestMethod.POST,
            path = path,
            headers = headers,
            body = body
        )
    }

    data class StubListInfo<T>(
        val items: List<T>
    )

    data class StubModel(
        var a: String,
        var b: Int,
        var c: Long,
        var d: Double
    )
}
