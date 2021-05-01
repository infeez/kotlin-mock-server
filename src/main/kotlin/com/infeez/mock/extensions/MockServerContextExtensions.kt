package com.infeez.mock.extensions

import com.infeez.mock.dsl.context.MockContext
import com.infeez.mock.dsl.context.MockMatcherContext
import com.infeez.mock.dsl.context.MockResponseContext
import com.infeez.mock.dsl.context.MockServerContext
import com.infeez.mock.matcher.RequestMatcher
import com.infeez.mock.util.RequestMethod

fun MockServerContext.mocks(block: MockContext.() -> Unit) {
    addAll(com.infeez.mock.dsl.mocks(block))
}

fun MockServerContext.mock(requestMethod: RequestMethod, url: String, mockBuilder: MockResponseContext.() -> Unit = {}) {
    add(com.infeez.mock.dsl.mock(requestMethod, url, mockBuilder))
}

fun MockServerContext.mock(url: String, mockBuilder: MockResponseContext.() -> Unit = {}) {
    add(com.infeez.mock.dsl.mock(url, mockBuilder))
}

fun MockServerContext.mock(requestMethod: RequestMethod, matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}) {
    add(com.infeez.mock.dsl.mock(requestMethod, matcher, mockBuilder))
}

fun MockServerContext.mock(matcher: MockMatcherContext.() -> RequestMatcher, mockBuilder: MockResponseContext.() -> Unit = {}) {
    add(com.infeez.mock.dsl.mock(matcher, mockBuilder))
}
