package io.github.infeez.kotlinmockserver.extensions

import io.github.infeez.kotlinmockserver.dsl.http.MockBuilder
import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.util.RequestMethod
import java.lang.reflect.Type

fun MockServerContext.mocks(block: MockContext.() -> Unit) {
    addAll(io.github.infeez.kotlinmockserver.dsl.http.mocks(block))
}

fun MockServerContext.mock(
    requestMethod: RequestMethod,
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
): Mock  {
    return io.github.infeez.kotlinmockserver.dsl.http.mock(requestMethod, url, mockBuilder).also(::add)
}

fun MockServerContext.mock(
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
): Mock  {
    return io.github.infeez.kotlinmockserver.dsl.http.mock(url, mockBuilder).also(::add)
}

fun MockServerContext.mock(
    requestMethod: RequestMethod,
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilderWrapper {
    return MockBuilderWrapper(this, io.github.infeez.kotlinmockserver.dsl.http.mock(requestMethod, matcher))
}

fun MockServerContext.mock(
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilderWrapper {
    return MockBuilderWrapper(this, io.github.infeez.kotlinmockserver.dsl.http.mock(matcher))
}

/**
 * Change mock response body. Provides DSL-context with response model [T] for change.
 *
 * @param from   - [Mock] for change.
 * @param change - DSL-context with mock response model [T]
 */
inline fun <reified T> MockServerContext.changeMockBody(from: Mock, change: T.() -> Unit) {
    val temp = mocks.find { it == from }?.mockWebResponse ?: error("Mock not found!")
    mocks.find { it == from }?.mockWebResponse = temp.copyResponse(change)
}

/**
 * Change mock response body. Provides DSL-context with response model [T] for change.
 * With class type for embedded generic.
 *
 * @param type   - [Type] type of class model.
 * @param from   - [Mock] for change.
 * @param change - DSL-context with mock response model [T]
 */
inline fun <T> MockServerContext.changeMockBody(type: Type, from: Mock, change: T.() -> Unit) {
    val temp = mocks.find { it == from }?.mockWebResponse ?: error("Mock not found!")
    mocks.find { it == from }?.mockWebResponse = temp.copyResponse(type, change)
}

/**
 * Change mock response params. Provides DSL-context for change.
 *
 * @param mock  - [Mock] a mock for change.
 * @param block - DLS-context for change mock params.
  */
fun  MockServerContext.change(mock: Mock, block: MockResponseContext.() -> Unit) {
    replace(mock, mock.copy(block))
}

class MockBuilderWrapper(
    private val context: MockServerContext,
    private val mockBuilder: MockBuilder
) {
    infix fun on(mockBuilderContext: MockResponseContext.() -> Unit): Mock {
        val mock = mockBuilder on mockBuilderContext
        context.add(mock)
        return mock
    }
}
