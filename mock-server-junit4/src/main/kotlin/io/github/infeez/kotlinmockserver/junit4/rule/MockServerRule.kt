package io.github.infeez.kotlinmockserver.junit4.rule

import io.github.infeez.kotlinmockserver.server.Server
import org.junit.rules.ExternalResource

class MockServerRule internal constructor(
    private val server: Server
) : ExternalResource() {

    override fun before() {
        server.start()
    }

    override fun after() {
        server.stop()
    }
}
