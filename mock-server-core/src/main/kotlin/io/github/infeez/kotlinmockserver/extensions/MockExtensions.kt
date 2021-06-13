package io.github.infeez.kotlinmockserver.extensions

import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.mock.Mock

fun Mock.copy(init: MockResponseContext.() -> Unit): Mock {
    return copy().let { mock ->
        val mrc = MockResponseContext()
        mrc.mwr = mock.mockWebResponse
        init(mrc)
        mock.mockWebResponse = mrc.mwr
        mock
    }
}