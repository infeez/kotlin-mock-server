package io.github.infeez.kotlinmockserver.extensions

import io.github.infeez.kotlinmockserver.dsl.http.MockBuilder
import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.util.RequestMethod

fun MockServerContext.mocks(block: MockContext.() -> Unit) {
    addAll(io.github.infeez.kotlinmockserver.dsl.http.mocks(block))
}

fun MockServerContext.mock(
    requestMethod: RequestMethod,
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
) {
    add(io.github.infeez.kotlinmockserver.dsl.http.mock(requestMethod, url, mockBuilder))
}

fun MockServerContext.mock(
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
) {
    add(io.github.infeez.kotlinmockserver.dsl.http.mock(url, mockBuilder))
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

class MockBuilderWrapper(
    private val context: MockServerContext,
    private val mockBuilder: MockBuilder
) {
    infix fun on(mockBuilderContext: MockResponseContext.() -> Unit) {
        context.add(mockBuilder on mockBuilderContext)
    }
}
