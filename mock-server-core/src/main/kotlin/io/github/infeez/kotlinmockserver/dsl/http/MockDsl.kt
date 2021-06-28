package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockMatcherContext
import io.github.infeez.kotlinmockserver.dsl.http.context.MockResponseContext
import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.util.RequestMethod

/**
 * DSL method for create and return HTTP mock by HTTP-method type and url equivalent.
 *
 * @param requestMethod - [RequestMethod] Set HTTP-method type
 * @param url           - [String] Set url for check incoming url in your client for mock response.
 * @param mockBuilder   - [MockResponseContext] DSL-context for build http mock response(code|headers|body|etc).
 *
 * @return a mock [Mock] type for use in MockServer. Also you need to add this created object to your mock list or MockServer mocks.
 */
fun mock(
    requestMethod: RequestMethod,
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
): Mock {
    return MockContext().mock(
        url = url,
        requestMethod = requestMethod,
        mockBuilder = mockBuilder
    )
}

/**
 * DSL method for create and return HTTP mock by url equivalent.
 *
 * @param url         - [String] Set url for check incoming url in your client for mock response.
 * @param mockBuilder - [MockResponseContext] DSL-context for build http mock response(code|headers|body|etc).
 *
 * @return a mock [Mock] type for use in MockServer. Also you need to add this created object to your mock list or MockServer mocks.
 */
fun mock(
    url: String,
    mockBuilder: MockResponseContext.() -> Unit = {}
): Mock {
    return MockContext().mock(
        url = url,
        mockBuilder = mockBuilder
    )
}

/**
 * DSL method for create and return HTTP mock by HTTP-method type and combined matcher.
 *
 * @param requestMethod - [RequestMethod] Set HTTP-method type
 * @param matcher       - [MockMatcherContext] DSL-context for build matcher for request in your client.
 *
 * @return a mock [Mock] type for use in MockServer. Also you need to add this created object to your mock list or MockServer mocks.
 */
fun mock(
    requestMethod: RequestMethod,
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilder {
    return MockBuilder(
        matcher = matcher,
        requestMethod = requestMethod
    )
}

/**
 * DSL method for create and return HTTP mock by combined matcher.
 *
 * @param matcher- [MockMatcherContext] DSL-context for build matcher for request in your client.
 *
 * @return a mock [Mock] type for use in MockServer. Also you need to add this created object to your mock list or MockServer mocks.
 */
fun mock(
    matcher: MockMatcherContext.() -> RequestMatcher
): MockBuilder {
    return MockBuilder(
        matcher = matcher
    )
}

class MockBuilder(
    val matcher: MockMatcherContext.() -> RequestMatcher,
    val requestMethod: RequestMethod = RequestMethod.ANY
) {
    infix fun on(mockBuilder: MockResponseContext.() -> Unit): Mock {
        return MockContext().mock(
            matcher = matcher,
            requestMethod = requestMethod,
            mockBuilder = mockBuilder
        )
    }
}