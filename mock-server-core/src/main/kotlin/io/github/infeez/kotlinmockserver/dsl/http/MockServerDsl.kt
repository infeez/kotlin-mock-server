package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.server.Server

fun mockServer(
    server: Server,
    settings: MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return MockServerContext(server, settings).apply(block)
}