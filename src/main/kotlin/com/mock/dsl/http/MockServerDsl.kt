package com.mock.dsl.http

import com.mock.MockServerSettings
import com.mock.dsl.http.context.MockServerContext
import com.mock.server.Configuration
import com.mock.server.Server
import com.mock.server.impl.NettyHttpServer
import com.mock.server.impl.OkHttpServer

fun okHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return customMockServer(OkHttpServer(serverConfiguration), settings, block)
}

fun nettyHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return customMockServer(NettyHttpServer(serverConfiguration), settings, block)
}

fun customMockServer(
    server: Server,
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return MockServerContext(server, settings).apply(block)
}
