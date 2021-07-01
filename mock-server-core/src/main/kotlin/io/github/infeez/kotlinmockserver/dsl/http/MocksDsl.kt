package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.mock.Mock

/**
 * Create list of mocks.
 *
 * This method helps to create a list of mocks easily.
 * Also next you need to add this list to a MockServer!
 *
 *  @param block - DSL-context to build mock.
 *
 *  @return [List] of [Mock].
 */
fun mocks(block: MockContext.() -> Unit): List<Mock> {
    return MockContext().apply(block).mocks
}
