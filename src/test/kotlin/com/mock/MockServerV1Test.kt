package com.mock

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mock.converter.ConverterFactory
import com.mock.dsl.http.context.MockServerContext
import com.mock.dsl.http.customMockServer
import com.mock.dsl.http.mock
import com.mock.dsl.http.nettyHttpMockServer
import com.mock.dsl.http.okHttpMockServer
import com.mock.extensions.mock
import com.mock.matcher.and
import com.mock.matcher.or
import com.mock.server.Configuration
import com.mock.server.Server
import com.mock.server.impl.NettyHttpServer
import com.mock.server.impl.OkHttpServer
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.util.Json
import io.github.rybalkinsd.kohttp.util.json
import okhttp3.Response
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MockServerV1Test {

    private val defaultResponse = "response string"

    @get:Rule
    val okHttpServer = okHttpMockServer {
        mock("/rule/okhttp/test") {
            body("its ok")
        }
    }

    @get:Rule
    val nettyServer = nettyHttpMockServer {
        mock("/rule/netty/test") {
            body("its ok")
        }
    }

    private val gsonConverterFactory = object : ConverterFactory {
        private val gson = Gson()
        override fun <T> from(value: String, type: Type): T {
            return gson.fromJson(value, type)
        }

        override fun <T> to(value: T): String {
            return gson.toJson(value)
        }
    }

    private var configuration = Configuration.default()

    private fun servers(): List<Server> {
        configuration = Configuration.default()
        return listOf(OkHttpServer(configuration), NettyHttpServer(configuration))
    }

    @Test
    fun `rule okhttp test`() {
        val response = httpGet {
            host = okHttpServer.server.configuration.host
            port = okHttpServer.server.configuration.port
            path = "/rule/okhttp/test"
        }

        assertEquals("its ok", response.body!!.string())
    }

    // почему-то два сервера стартуют с одинаковыми портами. TODO проверить правильность метода подбора портов
    @Test
    fun `rule netty test`() {
        val response = httpGet {
            host = nettyServer.server.configuration.host
            port = nettyServer.server.configuration.port
            path = "/rule/netty/test"
        }

        assertEquals("its ok", response.body!!.string())
    }

    @Test
    fun `mock test`() = runServers {
        mock("/base/mock/server") {
            headers("key" to "value")
            body(defaultResponse)
        }

        val response = httpGet {
            host = configuration.host
            port = configuration.port
            path = "/base/mock/server"
        }

        assertEquals(defaultResponse, response.body!!.string())
        assertEquals("value", response.headers["key"])
    }

    @Test
    fun `some mocks in one add block test`() = runServers {
        repeat(5) { index ->
            mock("/base/mock/server$index") {
                headers("key$index" to "value$index")
                body("response string$index")
            }

            val response = httpGet {
                host = configuration.host
                port = configuration.port
                path = "/base/mock/server$index"
            }

            assertEquals("response string$index", response.body!!.string())
            assertEquals("value$index", response.headers["key$index"])
        }
    }

    @Test
    fun `url mock query test`() = runServers {
        mock("/base/mock/server?param1=1&param2=2") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=2")
    }

    @Test
    fun `query empty value param test`() = runServers {
        mock("/base/mock/server?param1=1&param2=") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=")
    }

    @Test
    fun `reverse query test`() = runServers {
        mock("/base/mock/server?param2=2&param1=1") {
            body(defaultResponse)
        }

        getResultDefaultTest("/base/mock/server?param1=1&param2=2")
    }

    @Test
    fun `path param asterisk and query test`() = runServers {
        mock("/mock/*/url?param1=1&param2=2") {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/asterisk/url?param1=1&param2=2")
    }

    @Test
    fun `path param asterisk last and query test`() = runServers {
        mock("/mock/*") {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/132")
    }

    @Test
    fun `path eq matcher test`() = runServers {
        mock {
            path { eq("/mock/url") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `path startWith matcher test`() = runServers {
        mock {
            path { startWith("/mock") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `path endsWith matcher test`() = runServers {
        mock {
            path { endsWith("/url") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url")
    }

    @Test
    fun `param eq matcher test`() = runServers {
        mock {
            param("param") { eq("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `param startWith matcher test`() = runServers {
        mock {
            param("param") { startWith("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1234")
    }

    @Test
    fun `param endsWith matcher test`() = runServers {
        mock {
            param("param") { endsWith("4") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1234")
    }

    @Test
    fun `path eq and param eq matcher test`() = runServers {
        mock {
            path { eq("/mock/url") } and param("param") { endsWith("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `path eq(true) or param eq(false) matcher test`() = runServers {
        mock {
            path { eq("/mock/url") } or param("param") { endsWith("2") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `path eq(false) or param eq(true) matcher test`() = runServers {
        mock {
            path { eq("/some/path") } or param("param") { endsWith("1") }
        } on {
            body(defaultResponse)
        }

        getResultDefaultTest("/mock/url?param=1")
    }

    @Test
    fun `slash out in url`() = runServers {
        mock("url/without/first/slash") {
            body(defaultResponse)
        }

        getResultDefaultTest("/url/without/first/slash")
    }

    @Test
    fun `body param string test`() = runServers {
        mock {
            path { eq("/mock/url") } and body { eq("request body string") }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url", "request body string")
    }

    @Test
    fun `body param converter full json test`() = runServers {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "a" && b == 1 && c == 2L && d == 3.0 }
            }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url") {
            "a" to "a"
            "b" to 1
            "c" to 2L
            "d" to 3.0
        }
    }

    @Test
    fun `body param converter one field json test`() = runServers {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "a" }
            }
        } on {
            body(defaultResponse)
        }

        postResultDefaultTest("/mock/url") {
            "a" to "a"
            "b" to 1
            "c" to 2L
            "d" to 3.0
        }
    }

    @Test
    fun `body param converter no one field json test`() = runServers {
        mock {
            path { eq("/mock/url") } and body {
                bodyMarch<StubModel> { a == "aabb" }
            }
        } on {
            body(defaultResponse)
        }

        httpPost {
            host = configuration.host
            port = configuration.port
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            path = "/mock/url"
        }.run {
            assertNotEquals("response string", body!!.string())
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

        runServers {
            addAll(mock1, mock2)

            var response = httpPost {
                host = configuration.host
                port = configuration.port
                path = "/mock/url"
            }

            assertEquals("$defaultResponse 1", response.body!!.string())

            replace(mock1, mock2)

            response = httpPost {
                host = configuration.host
                port = configuration.port
                path = "/mock/url"
            }

            assertEquals("$defaultResponse 2", response.body!!.string())
        }
    }

    @Test
    fun `remove mock test`() {
        val mock = mock { path { eq("/mock/url") } } on {
            body(defaultResponse)
        }

        runServers {
            add(mock)

            var response = httpPost {
                host = configuration.host
                port = configuration.port
                path = "/mock/url"
            }

            assertEquals(defaultResponse, response.body!!.string())

            remove(mock)

            response = httpPost {
                host = configuration.host
                port = configuration.port
                path = "/mock/url"
            }

            assertNotEquals(defaultResponse, response.body!!.string())
        }
    }

    @Test
    fun `change mock response body test`() = runServers {
        val mock = com.mock.dsl.http.mock { path { eq("/mock/url") } } on {
            body(
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            )
        }

        add(mock)

        var response = httpPost {
            host = configuration.host
            port = configuration.port
            path = "/mock/url"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body!!.string())

        changeMockBody<StubModel>(mock) {
            d = 55.5
        }

        response = httpPost {
            host = configuration.host
            port = configuration.port
            path = "/mock/url"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body!!.string())
    }

    @Test
    fun `change mock response body with generic test`() = runServers {
        val mock = com.mock.dsl.http.mock { path { eq("/mock/url") } } on {
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

        httpPost {
            host = configuration.host
            port = configuration.port
            path = "/mock/url"
        }.run {
            assertEquals("""{"items":[{"a":"b","b":2,"c":3,"d":4.0}]}""", body!!.string())
        }
    }

    @Test
    fun `copy mock test`() = runServers {
        val mock1 = com.mock.dsl.http.mock { path { eq("/mock/url") } } on {
            code(201)
            headers {
                "a" to "123"
            }
            delay(100, MILLISECONDS)
            body(
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            )
        }

        val mock2 = mock1.copy {
            body(
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 55.5
                }
            )
        }

        add(mock2)

        val response = httpPost {
            host = configuration.host
            port = configuration.port
            path = "/mock/url"
        }

        assertEquals(100, mock2.mockWebResponse.mockWebResponseParams.delay)

        assertEquals("123", response.headers["a"])
        assertEquals(201, response.code)
        assertEquals("""{"a":"a","b":1,"c":2,"d":55.5}""", response.body!!.string())
    }

    @Test
    fun `copyResponse mock not affect copied mock test`() = runServers {
        val mock1 = com.mock.dsl.http.mock { path { eq("/mock/url") } } on {
            code(201)
            headers {
                "a" to "123"
            }
            delay(100, MILLISECONDS)
            body(
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            )
        }

        add(mock1)

        mock1.copy {
            body(
                json {
                    "a" to "a"
                    "b" to 121231
                    "c" to 2L
                    "d" to 55.5
                }
            )
        }

        val response = httpPost {
            host = configuration.host
            port = configuration.port
            path = "/mock/url"
        }

        assertEquals("""{"a":"a","b":1,"c":2,"d":3.0}""", response.body!!.string())
    }

    @Test
    fun `body param converter one url multiple time test`() = runServers {
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "a" } } } on {
            body(("response string a"))
        }
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "b" } } } on {
            body(("response string b"))
        }
        mock { path { eq("/some/path") } and body { bodyMarch<StubModel> { a == "c" } } } on {
            body(("response string c"))
        }

        var response = httpPost {
            host = configuration.host
            port = configuration.port
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            path = "/some/path"
        }

        assertEquals("response string a", response.body!!.string())

        response = httpPost {
            host = configuration.host
            port = configuration.port
            body {
                json {
                    "a" to "b"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            path = "/some/path"
        }

        assertEquals("response string b", response.body!!.string())

        response = httpPost {
            host = configuration.host
            port = configuration.port
            body {
                json {
                    "a" to "c"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            path = "/some/path"
        }

        assertEquals("response string c", response.body!!.string())
    }

    @Test
    fun `bodyEq test`() = runServers {
        mock { path { eq("/some/path") } and body { bodyEq<StubModel>("""{"a":"a","b":1,"c":2,"d":3.0}""") } } on {
            body(("response string a"))
        }

        val response = httpPost {
            host = configuration.host
            port = configuration.port
            body {
                json {
                    "a" to "a"
                    "b" to 1
                    "c" to 2L
                    "d" to 3.0
                }
            }
            path = "/some/path"
        }

        assertEquals("response string a", response.body!!.string())
    }

    @Ignore("Need to add get request from server")
    @Test
    fun `double read body test`() = runServers {
        mock { path { eq("/some/path") } } on {
            body(defaultResponse)
        }

        httpPost {
            host = configuration.host
            port = configuration.port
            path = "/some/path"
            body {
                string("request string a")
            }
        }

//        assertEquals("request string a", takeRequest().body.readUtf8())
    }

    private fun getResultDefaultTest(url: String) {
        getResultTest(url) {
            assertEquals(defaultResponse, body!!.string())
        }
    }

    private fun postResultDefaultTest(url: String, bodyStr: String? = null) {
        postResultTest(url, bodyStr) {
            assertEquals(defaultResponse, body!!.string())
        }
    }

    private fun postResultDefaultTest(url: String, bodyJson: (Json.() -> Unit)? = null) {
        postResultTest(url, bodyJson = bodyJson) {
            assertEquals(defaultResponse, body!!.string())
        }
    }

    private fun getResultTest(url: String, result: Response.() -> Unit) {
        result(
            httpGet {
                host = configuration.host
                port = configuration.port
                path = url
            }
        )
    }

    private fun postResultTest(url: String, bodyStr: String? = null, bodyJson: (Json.() -> Unit)? = null, result: Response.() -> Unit) {
        result(
            httpPost {
                host = configuration.host
                port = configuration.port
                bodyStr?.also {
                    body {
                        string(it)
                    }
                }
                bodyJson?.also {
                    if (bodyStr != null) {
                        error("bodyStr not null!")
                    }

                    body {
                        json(it)
                    }
                }
                path = url
            }
        )
    }

    private fun runServers(block: MockServerContext.() -> Unit) {
        servers().forEach {
            it.start()
            customMockServer(
                it,
                {
                    converterFactory = gsonConverterFactory
                },
                block
            )
            it.stop()
            println("${it.javaClass.name} OK")
        }
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
