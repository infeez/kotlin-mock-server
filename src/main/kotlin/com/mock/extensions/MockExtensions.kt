package com.mock.extensions

import com.mock.dsl.http.context.MockResponseContext
import com.mock.mock.Mock

fun Mock.copy(init: MockResponseContext.() -> Unit): Mock {
    return copy().let { mock ->
        val mrc = MockResponseContext()
        mrc.mwr = mock.mockWebResponse
        init(mrc)
        mock.mockWebResponse = mrc.mwr
        mock
    }
}