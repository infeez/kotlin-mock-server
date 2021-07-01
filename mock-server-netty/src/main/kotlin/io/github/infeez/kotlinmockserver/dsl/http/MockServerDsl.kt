package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import io.github.infeez.kotlinmockserver.server.NettyHttpServer
import io.github.infeez.kotlinmockserver.server.ServerConfiguration

/**
 * Create a Netty mock server instance.
 *
 * @param serverConfiguration - [ServerConfiguration] server configuration.
 * @param mockConfiguration   - configuration of mock server.
 * @param block               - DLS-context of [MockServerContext] for build mocks.
 */
fun nettyHttpMockServer(
    serverConfiguration: ServerConfiguration = ServerConfiguration.default(),
    mockConfiguration: MockConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return mockServer(NettyHttpServer(serverConfiguration), mockConfiguration, block)
}
