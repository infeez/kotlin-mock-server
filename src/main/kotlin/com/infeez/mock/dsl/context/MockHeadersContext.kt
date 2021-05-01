package com.infeez.mock.dsl.context

class MockHeadersContext {
    internal val headers = mutableMapOf<String, String>()

    infix fun <T> String.to(value: T) {
        headers[this] = value.toString()
    }
}
