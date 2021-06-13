package io.github.infeez.kotlinmockserver.junit4

import io.github.infeez.kotlinmockserver.dsl.http.context.MockServerContext
import io.github.infeez.kotlinmockserver.server.Server
import org.junit.rules.ExternalResource

class MockServerRule(
    private val mockServerContext: MockServerContext
) : ExternalResource() {

    override fun before() {
        //server.start()
    }

    override fun after() {
        //server.stop()
    }
}
