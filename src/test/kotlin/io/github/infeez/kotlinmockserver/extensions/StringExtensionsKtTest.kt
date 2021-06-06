package io.github.infeez.kotlinmockserver.extensions

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringExtensionsKtTest {

    @Test
    fun `extract query params test`() {
        var url = "https://www.google.com/?a=1&b=2&c=a"
        var result = url.extractQueryParams()

        assertEquals("1", result["a"])
        assertEquals("2", result["b"])
        assertEquals("a", result["c"])

        url = "https://www.google.com/?a=1"
        result = url.extractQueryParams()

        assertEquals("1", result["a"])

        url = "https://www.google.com/"
        result = url.extractQueryParams()

        assertEquals(0, result.size)

        url = "https://www.google.com/?a="
        result = url.extractQueryParams()

        assertEquals(0, result.size)

        url = "https://www.google.com/?a=&b=&с="
        result = url.extractQueryParams()

        assertEquals(0, result.size)

        url = "https://www.google.com/?a=&b=&c=1"
        result = url.extractQueryParams()

        assertEquals(1, result.size)
        assertEquals("1", result["c"])

        url = "https://www.google.com/?a=&b=1&с="
        result = url.extractQueryParams()

        assertEquals(1, result.size)
        assertEquals("1", result["b"])

        url = "https://www.google.com/?a=1&b=&с="
        result = url.extractQueryParams()

        assertEquals(1, result.size)
        assertEquals("1", result["a"])

        url = "https://www.google.com/?a=&&&&&"
        result = url.extractQueryParams()

        assertEquals(0, result.size)
    }

    @Test
    fun `removeFirstAndLastSlashInUrl test`() {
        assertEquals("b", "/b/".removeFirstAndLastSlashInUrl())
        assertEquals("a/b", "a/b".removeFirstAndLastSlashInUrl())
        assertEquals("a/b", "/a/b/".removeFirstAndLastSlashInUrl())
        assertEquals("a/b/c/d", "/a/b/c/d/".removeFirstAndLastSlashInUrl())
    }

    @Test
    fun `checkUrlParamWithAsterisk test`() {
        assertTrue {
            "a/*/b".checkUrlParamWithAsterisk("a/123/b")
            "*/*/b".checkUrlParamWithAsterisk("a/123/b")
            "*/*/*".checkUrlParamWithAsterisk("a/123/b")
            "*/b".checkUrlParamWithAsterisk("123/b")
            "*/*".checkUrlParamWithAsterisk("123/b")
            "*".checkUrlParamWithAsterisk("123")
            "a/b/c".checkUrlParamWithAsterisk("/a/b/c/")
            "a/b".checkUrlParamWithAsterisk("/a/b/")
            "a".checkUrlParamWithAsterisk("/a/")
            "a/b/c".checkUrlParamWithAsterisk("a/b/c")
            "a/b".checkUrlParamWithAsterisk("a/b")
            "a".checkUrlParamWithAsterisk("a")
            "a/123/*".checkUrlParamWithAsterisk("a/123/b")
        }
    }
}