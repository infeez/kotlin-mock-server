package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import io.github.infeez.kotlinmockserver.server.OkHttpServer
import io.github.infeez.kotlinmockserver.server.ServerConfiguration

/**
 * Create a okHttp mock server instance.
 *
 * @param serverConfiguration - [ServerConfiguration] server configuration.
 * @param mockConfiguration   - configuration of mock server.
 * @param block               - DLS-context of [MockServerContext] for build mocks.
 */
fun okHttpMockServer(
    serverConfiguration: ServerConfiguration = ServerConfiguration.default(),
    mockConfiguration: MockConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return mockServer(OkHttpServer(serverConfiguration), mockConfiguration, block)
}
