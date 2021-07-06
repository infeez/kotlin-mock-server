package io.github.infeez.kotlinmockserver.junit4

import io.github.infeez.kotlinmockserver.junit4.extensions.asRule
import io.github.infeez.kotlinmockserver.server.Server
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class MockServerRuleTest {

    @Test
    fun ruleTest() {
        val server = mock<Server>()

        server.asRule().apply(
            object : Statement() {
                override fun evaluate() {
                    // do nothing
                }
            },
            Description.EMPTY
        ).evaluate()

        verify(server).start()
        verify(server).stop()
    }
}
