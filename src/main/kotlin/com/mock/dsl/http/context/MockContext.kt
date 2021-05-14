package com.mock.dsl.http.context

import com.mock.matcher.RequestMatcher
import com.mock.mock.Mock
import com.mock.mock.impl.MatcherMock
import com.mock.mock.impl.UrlMock
import com.mock.util.RequestMethod

class MockContext {

    internal val mocks = ArrayList<Mock>(1)

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
