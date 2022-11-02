package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.matcher.RequestMatcher
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.mock.impl.MatcherMock
import io.github.infeez.kotlinmockserver.mock.impl.UrlMock
import io.github.infeez.kotlinmockserver.util.RequestMethod

/**
 * A base class for creating a mock.
 */
class MockContext {

    internal val mocks = ArrayList<Mock>(1)

    /**
     * A method to create a mock for direct url comparison.
     *
     * @param url           - [String] client's call for a link to mock
     * @param requestMethod - [RequestMethod] Http-method type for mock. Optional.
     * @param mockBuilder   - [MockResponseContext] DSL-context to create mock response. Optional.
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

    fun mockLazy(
        url: String,
        requestMethod: RequestMethod = RequestMethod.ANY,
        mockBuilder: MockResponseContext.() -> Unit = {}
    ): Lazy<Mock> {
        return lazy {
            mock(
                url = url,
                requestMethod = requestMethod,
                mockBuilder = mockBuilder
            )
        }
    }

    /**
     * A method to create a mock using the comparison parameters.
     *
     * @param matcher       - [MockMatcherContext] DSL-context to create comparison parameters.
     * @param requestMethod - [RequestMethod] Http-method type for mock. Optional.
     * @param mockBuilder   - [MockResponseContext] DSL-context to create mock response. Optional.
     */
    @Deprecated(
        message = "",
        replaceWith = ReplaceWith("", "")
    )
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

    fun mock(
        matcher: RequestMatcher,
        requestMethod: RequestMethod = RequestMethod.ANY,
        mockBuilder: MockResponseContext.() -> Unit = {}
    ): Mock {
        return MatcherMock(requestMethod, matcher).apply {
            mockWebResponse = MockResponseContext().apply(mockBuilder).mwr
        }.also {
            mocks.add(it)
        }
    }

    fun mockLazy(
        matcher: RequestMatcher,
        requestMethod: RequestMethod = RequestMethod.ANY,
        mockBuilder: MockResponseContext.() -> Unit = {}
    ): Lazy<Mock> {
        return lazy {
            mock(
                matcher = matcher,
                requestMethod = requestMethod,
                mockBuilder = mockBuilder
            )
        }
    }

    operator fun Mock.unaryPlus() {
        mocks.add(this)
    }

    operator fun Mock.unaryMinus() {
        mocks.remove(this)
    }
}
