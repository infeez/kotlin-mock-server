package com.infeez.mock

sealed class MockServerBehavior {
    object ErrorWhenMockNotFound : MockServerBehavior()
    object PassWhenMockNotFound : MockServerBehavior()
}
