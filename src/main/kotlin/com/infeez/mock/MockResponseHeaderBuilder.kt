package com.infeez.mock

import okhttp3.mockwebserver.MockResponse

@Deprecated(
    message = "Please use new API for mock. See more in documentation.",
    replaceWith = ReplaceWith("MockMatcherContext", "com.infeez.mock.dsl.context")
)
class MockResponseHeaderBuilder(private val mockResponse: MockResponse) {
    infix fun String.withValue(value: Any) {
        mockResponse.addHeader(this, value)
    }
}
