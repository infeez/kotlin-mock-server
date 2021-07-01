package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import io.github.infeez.kotlinmockserver.server.Server

/**
 * Create mock server instance.
 * You need to use with custom server or implementation in the library.
 *
 * @param server            - implementation of [Server] for use in mock server.
 * @param mockConfiguration - configuration of mock server.
 * @param block             - DLS-context of [MockServerContext] for build mocks.
 */
fun mockServer(
    server: Server,
    mockConfiguration: MockConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return MockServerContext(server, mockConfiguration).apply(block)
}
