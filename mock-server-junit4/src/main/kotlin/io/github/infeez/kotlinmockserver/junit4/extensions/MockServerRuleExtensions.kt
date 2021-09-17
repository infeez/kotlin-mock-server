package io.github.infeez.kotlinmockserver.junit4.extensions

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.junit4.rule.MockServerRule
import io.github.infeez.kotlinmockserver.server.Server

/**
 * Create rule for use with JUnit4 Rule annotation for server lifecycle.
 */
fun MockServerContext.asRule(): MockServerRule {
    return server.asRule()
}

/**
 * Create rule for use with JUnit4 Rule annotation for server lifecycle.
 */
fun Server.asRule(): MockServerRule {
    return MockServerRule(this)
}
