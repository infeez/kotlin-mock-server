package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * This class realise DSL-context for build mock response params.
 *
 */
class MockResponseContext {
    internal var mwr = MockWebResponse(code = 200)

    /**
     * Set delay to mock response.
     *
     *
     * @param time     - [Long] delay time.
     * @param timeUnit - [TimeUnit] delay time unit. MILLISECONDS by default.
     */
    fun delay(time: Long, timeUnit: TimeUnit = MILLISECONDS) {
        mwr = mwr.copy(mockWebResponseParams = mwr.mockWebResponseParams.copy(delay = timeUnit.toMillis(time)))
    }

    /**
     * Set response HTTP-code.
     *
     * @param code - [Int] HTTP-code.
     */
    fun code(code: Int) {
        mwr = mwr.copy(code = code)
    }

    /**
     * Set header pair as vararg.
     *
     * @param headers - [Pair] header pair string and string.
     */
    fun headers(vararg headers: Pair<String, String>) {
        mwr = mwr.copy(headers = headers.toMap())
    }

    /**
     * Set header by DLS-context
     *
     * @param init - DSL-context block.
     */
    fun headers(init: MockHeadersContext.() -> Unit) {
        mwr = mwr.copy(headers = MockHeadersContext().apply(init).headers)
    }

    /**
     * Set response body.
     *
     * @param body [String] as a body.
     */
    fun body(body: String) {
        mwr = mwr.copy(body = body)
    }

    /**
     * Set response body.
     *
     * @param file [File] as a body.
     */
    fun body(file: File) {
        body(FileInputStream(file))
    }

    /**
     * Set response body.
     *
     * @param inputStream [InputStream] as a body.
     */
    fun body(inputStream: InputStream) {
        body(inputStream.use { stream -> stream.bufferedReader().use { it.readText() } })
    }

    /**
     * Set empty response body.
     *
     */
    fun emptyBody() {
        body("")
    }
}
