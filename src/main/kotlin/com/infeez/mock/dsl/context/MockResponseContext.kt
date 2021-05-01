package com.infeez.mock.dsl.context

import com.infeez.mock.mockmodel.MockWebResponse
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

class MockResponseContext {
    internal var mwr = MockWebResponse(code = 200)

    fun delay(time: Long, timeUnit: TimeUnit = MILLISECONDS) {
        mwr = mwr.copy(mockWebResponseParams = mwr.mockWebResponseParams.copy(delay = timeUnit.toMillis(time)))
    }

    fun code(code: Int) {
        mwr = mwr.copy(code = code)
    }

    fun headers(vararg headers: Pair<String, String>) {
        mwr = mwr.copy(headers = headers.toMap())
    }

    fun headers(init: MockHeadersContext.() -> Unit) {
        mwr = mwr.copy(headers = MockHeadersContext().apply(init).headers)
    }

    fun body(body: String) {
        mwr = mwr.copy(body = body)
    }

    fun body(file: File) {
        body(FileInputStream(file))
    }

    fun body(inputStream: InputStream) {
        body(inputStream.use { stream -> stream.bufferedReader().use { it.readText() } })
    }

    fun emptyBody() {
        body("")
    }
}
