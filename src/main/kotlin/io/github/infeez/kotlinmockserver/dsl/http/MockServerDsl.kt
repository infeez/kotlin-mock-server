package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.server.Configuration
import io.github.infeez.kotlinmockserver.server.Server
import io.github.infeez.kotlinmockserver.server.impl.NettyHttpServer
import io.github.infeez.kotlinmockserver.server.impl.OkHttpServer

fun okHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: io.github.infeez.kotlinmockserver.MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return customMockServer(OkHttpServer(serverConfiguration), settings, block)
}

fun nettyHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: io.github.infeez.kotlinmockserver.MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return customMockServer(NettyHttpServer(serverConfiguration), settings, block)
}

fun customMockServer(
    server: Server,
    settings: io.github.infeez.kotlinmockserver.MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return MockServerContext(server, settings).apply(block)
}