package com.mock.mock

import com.mock.mockmodel.MockWebResponse
import com.mock.util.RequestMethod

abstract class Mock(
    private val requestMethod: RequestMethod
) {

    lateinit var mockWebResponse: MockWebResponse

    open fun isCoincided(path: String, method: String?, body: String? = null, headers: Map<String, String> = emptyMap()): Boolean {
        return checkRequestMethod(requestMethod, method)
    }

    open fun copy(): Mock {
        TODO("Not implemented!")
    }

    private fun checkRequestMethod(src: RequestMethod, trg: String?): Boolean {
        return src == RequestMethod.ANY || src.method.equals(trg, ignoreCase = true)
    }
}