package com.mock.extensions

import com.mock.dsl.http.MockBuilder
import com.mock.dsl.http.context.MockContext
import com.mock.dsl.http.context.MockMatcherContext
import com.mock.dsl.http.context.MockResponseContext
import com.mock.dsl.http.context.MockServerContext
import com.mock.matcher.RequestMatcher
import com.mock.util.RequestMethod

fun MockServerContext.mocks(block: MockContext.() -> Unit) {
    addAll(com.mock.dsl.http.mocks(block))
}

fun MockServerContext.mock(
    requestMethod: RequestMethod,
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
) {
    add(com.mock.dsl.http.mock(requestMethod, url, mockBuilder))
}

fun MockServerContext.mock(
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
) {
    add(com.mock.dsl.http.mock(url, mockBuilder))
}

fun MockServerContext.mock(
    requestMethod: RequestMethod,
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilderWrapper {
    return MockBuilderWrapper(this, com.mock.dsl.http.mock(requestMethod, matcher))
}

fun MockServerContext.mock(
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilderWrapper {
    return MockBuilderWrapper(this, com.mock.dsl.http.mock(matcher))
}

class MockBuilderWrapper(
    private val context: MockServerContext,
    private val mockBuilder: MockBuilder
) {
    infix fun on(mockBuilderContext: MockResponseContext.() -> Unit) {
        context.add(mockBuilder on mockBuilderContext)
    }
}