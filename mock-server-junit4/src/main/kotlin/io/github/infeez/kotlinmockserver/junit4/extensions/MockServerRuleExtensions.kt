package io.github.infeez.kotlinmockserver.junit4.extensions

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.junit4.rule.MockServerRule
import io.github.infeez.kotlinmockserver.server.Server

fun MockServerContext.asRule(): MockServerRule {
    return server.asRule()
}

fun Server.asRule(): MockServerRule {
    return MockServerRule(this)
}