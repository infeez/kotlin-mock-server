package com.infeez.mock.utils

import kotlin.test.assertEquals
import org.junit.Test

internal class UtilsKtTest {

    @Test
    fun `extract query params test`() {
        var url = "https://www.google.com/?a=1&b=2&c=a"
        var result = extractQueryParams(url)

        assertEquals("1", result["a"])
        assertEquals("2", result["b"])
        assertEquals("a", result["c"])

        url = "https://www.google.com/?a=1"
        result = extractQueryParams(url)

        assertEquals("1", result["a"])

        url = "https://www.google.com/"
        result = extractQueryParams(url)

        assertEquals(0, result.size)

        url = "https://www.google.com/?a="
        result = extractQueryParams(url)

        assertEquals(0, result.size)

        url = "https://www.google.com/?a=&b=&с="
        result = extractQueryParams(url)

        assertEquals(0, result.size)

        url = "https://www.google.com/?a=&b=&c=1"
        result = extractQueryParams(url)

        assertEquals(1, result.size)
        assertEquals("1", result["c"])

        url = "https://www.google.com/?a=&b=1&с="
        result = extractQueryParams(url)

        assertEquals(1, result.size)
        assertEquals("1", result["b"])

        url = "https://www.google.com/?a=1&b=&с="
        result = extractQueryParams(url)

        assertEquals(1, result.size)
        assertEquals("1", result["a"])

        url = "https://www.google.com/?a=&&&&&"
        result = extractQueryParams(url)

        assertEquals(0, result.size)
    }
}
