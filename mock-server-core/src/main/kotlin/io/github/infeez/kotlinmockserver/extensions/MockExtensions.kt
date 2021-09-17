package io.github.infeez.kotlinmockserver.extensions

import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.mock.Mock

/**
 * Copying of the mock with the possibility of changes.
 *
 * @param block - DSL-context with [MockResponseContext] for change.
 */
fun Mock.copy(block: MockResponseContext.() -> Unit): Mock {
    return copy().let { mock ->
        val mrc = MockResponseContext()
        mrc.mwr = mock.mockWebResponse
        block(mrc)
        mock.mockWebResponse = mrc.mwr
        mock
    }
}

/**
 * Change mock response with DSL-context.
 *
 * @param block - DSL-context with [MockResponseContext] for change.
 */
fun Mock.changeResponse(block: MockResponseContext.() -> Unit) {
    val mrc = MockResponseContext()
    mrc.mwr = mockWebResponse
    block(mrc)
    mockWebResponse = mrc.mwr
}
