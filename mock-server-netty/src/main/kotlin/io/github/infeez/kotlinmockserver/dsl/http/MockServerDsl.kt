package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.server.Configuration
import io.github.infeez.kotlinmockserver.server.NettyHttpServer

fun nettyHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return mockServer(NettyHttpServer(serverConfiguration), settings, block)
}
