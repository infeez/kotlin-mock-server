package com.mock.mock

import com.mock.dsl.http.context.MockResponseContext
import com.mock.mockmodel.MockWebResponse
import com.mock.util.RequestMethod

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

    // TODO точно этому методу место тут? Абстрактный Mock знает про MockResponseContext
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
