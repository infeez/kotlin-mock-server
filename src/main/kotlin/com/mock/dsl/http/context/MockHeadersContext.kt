package com.mock.dsl.http.context

/**
 * This class realise DSL-context to add and create HTTP-headers.
 *
 */
class MockHeadersContext {
    internal val headers = mutableMapOf<String, String>()

    /**
     * Create header pair and add to headers list.
     *
     * First infix param always [String].
     * @param value - [T] second infix param may of any type but make sure you override toString(). Not needed for primitives.
     */
    infix fun <T> String.to(value: T) {
        headers[this] = value.toString()
    }
}