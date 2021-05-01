package com.infeez.mock.dsl

import com.infeez.mock.dsl.context.MockContext
import com.infeez.mock.mock.Mock

fun mocks(block: MockContext.() -> Unit): List<Mock> {
    return MockContext().apply(block).mocks
}
