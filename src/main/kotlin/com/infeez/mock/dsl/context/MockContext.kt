package com.infeez.mock.dsl.context

import com.infeez.mock.matcher.RequestMatcher
import com.infeez.mock.mock.Mock
import com.infeez.mock.mock.impl.MatcherMock
import com.infeez.mock.mock.impl.UrlMock
import com.infeez.mock.util.RequestMethod

class MockContext {

    internal val mocks = mutableListOf<Mock>()

    fun mock(requestMethod: RequestMethod, url: String, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
        return UrlMock(requestMethod, url).apply {
            mockWebResponse = MockResponseContext().apply(mockBuilder).mwr
        }.also {
            mocks.add(it)
        }
    }

    fun mock(url: String, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
        return mock(RequestMethod.ANY, url, mockBuilder)
    }

    fun mock(requestMethod: RequestMethod, matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
        return MatcherMock(requestMethod, matcher(MockMatcherContext())).apply {
            mockWebResponse = MockResponseContext().apply(mockBuilder).mwr
        }.also {
            mocks.add(it)
        }
    }

    fun mock(matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
        return mock(RequestMethod.ANY, matcher, mockBuilder)
    }
}
