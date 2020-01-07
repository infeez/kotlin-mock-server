package com.infeez.mock.extensions

import kotlin.test.assertEquals
import org.junit.Test

class ExtensionsKtTest {

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
}
