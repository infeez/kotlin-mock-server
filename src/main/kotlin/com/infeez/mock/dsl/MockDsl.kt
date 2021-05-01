package com.infeez.mock.dsl

import com.infeez.mock.dsl.context.MockContext
import com.infeez.mock.dsl.context.MockMatcherContext
import com.infeez.mock.dsl.context.MockResponseContext
import com.infeez.mock.matcher.RequestMatcher
import com.infeez.mock.mock.Mock
import com.infeez.mock.util.RequestMethod

fun mock(requestMethod: RequestMethod, url: String, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
    return MockContext().mock(requestMethod, url, mockBuilder)
}

fun mock(url: String, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
    return MockContext().mock(url, mockBuilder)
}

fun mock(requestMethod: RequestMethod, matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
    return MockContext().mock(requestMethod, matcher, mockBuilder)
}

fun mock(matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}): Mock {
    return MockContext().mock(matcher, mockBuilder)
}
