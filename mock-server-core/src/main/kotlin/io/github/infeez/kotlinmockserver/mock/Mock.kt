package io.github.infeez.kotlinmockserver.mock

import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import io.github.infeez.kotlinmockserver.util.RequestMethod

abstract class Mock(
    private val requestMethod: RequestMethod
) {

    lateinit var mockWebResponse: MockWebResponse

    open fun isCoincided(
        path: String,
        method: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap()
    ): Boolean {
        return checkRequestMethod(requestMethod, method)
    }

    abstract fun copy(): Mock

    private fun checkRequestMethod(
        src: RequestMethod,
        trg: String
    ): Boolean {
        return src == RequestMethod.ANY || src.method.equals(trg, ignoreCase = true)
    }
}
