package io.github.infeez.kotlinmockserver.dsl.http

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.server.Configuration
import io.github.infeez.kotlinmockserver.server.OkHttpServer

fun okHttpMockServer(
    serverConfiguration: Configuration = Configuration.default(),
    settings: MockServerConfiguration.() -> Unit = {},
    block: MockServerContext.() -> Unit = {}
): MockServerContext {
    return mockServer(OkHttpServer(serverConfiguration), settings, block)
}