package com.mock.dsl.http.context

import com.mock.matcher.RequestMatcher
import com.mock.mock.Mock
import com.mock.mock.impl.MatcherMock
import com.mock.mock.impl.UrlMock
import com.mock.util.RequestMethod

/**
 * A base class for creating a mock.
 *
 */
class MockContext {

    internal val mocks = ArrayList<Mock>(1)

    /**
     * A method to create a mock for direct url comparison.
     *
     * @param url           - [String] client's call for a link to mock
     * @param requestMethod - [RequestMethod] Http-method type for mock. Optional.
     * @param mockBuilder   - [MockResponseContext] DSL-context to create mock response. Optional.
     *
     */
    fun mock(
        url: String,
        requestMethod: RequestMethod = RequestMethod.ANY,
        mockBuilder: MockResponseContext.() -> Unit = {}
    ): Mock {
        return UrlMock(requestMethod, url).apply {
            mockWebResponse = MockResponseContext().apply(mockBuilder).mwr
        }.also {
            mocks.add(it)
        }
    }

    /**
     * A method to create a mock using the comparison parameters.
     *
     * @param matcher       - [MockMatcherContext] DSL-context to create comparison parameters.
     * @param requestMethod - [RequestMethod] Http-method type for mock. Optional.
     * @param mockBuilder   - [MockResponseContext] DSL-context to create mock response. Optional.
     *
     */
    fun mock(
        matcher: MockMatcherContext.() -> RequestMatcher,
        requestMethod: RequestMethod = RequestMethod.ANY,
        mockBuilder: MockResponseContext.() -> Unit = {}
    ): Mock {
        return MatcherMock(requestMethod, matcher(MockMatcherContext())).apply {
            mockWebResponse = MockResponseContext().apply(mockBuilder).mwr
        }.also {
            mocks.add(it)
        }
    }
}