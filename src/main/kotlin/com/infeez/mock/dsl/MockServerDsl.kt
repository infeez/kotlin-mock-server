package com.infeez.mock.dsl

import com.infeez.mock.MockServerSettings
import com.infeez.mock.dsl.context.MockServerContext
import com.infeez.mock.server.Configuration
import com.infeez.mock.server.Server
import com.infeez.mock.server.impl.NettyServer
import com.infeez.mock.server.impl.OkHttpServer

fun okHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return customMockServer(OkHttpServer(serverConfiguration), settings, block)
}

fun nettyMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return customMockServer(NettyServer(serverConfiguration), settings, block)
}

fun customMockServer(
    server: Server,
    settings: MockServerSettings.() -> Unit = {},
    block: MockServerContext.() -> Unit
): MockServerContext {
    return MockServerContext(server, settings).apply(block)
}
