package com.infeez.mock.mock

import com.infeez.mock.dsl.context.MockResponseContext
import com.infeez.mock.mockmodel.MockWebResponse
import com.infeez.mock.util.RequestMethod

abstract class Mock(
    private val requestMethod: RequestMethod
) {

    lateinit var mockWebResponse: MockWebResponse

    open fun isCoincided(path: String, method: String?, body: String? = null): Boolean {
        return checkRequestMethod(requestMethod, method)
    }

    open fun copy(): Mock {
        TODO("Not implemented!")
    }

    open fun copy(init: MockResponseContext.() -> Unit): Mock {
        return copy().let { mock ->
            val mrc = MockResponseContext()
            mrc.mwr = mock.mockWebResponse
            init(mrc)
            mock.mockWebResponse = mrc.mwr
            mock
        }
    }

    private fun checkRequestMethod(src: RequestMethod, trg: String?): Boolean {
        return src == RequestMethod.ANY || src.method.equals(trg, ignoreCase = true)
    }
}
