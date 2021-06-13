package io.github.infeez.kotlinmockserver.junit4

import io.github.infeez.kotlinmockserver.dsl.http.okHttpMockServer
import org.junit.Rule
import org.junit.Test

class MockServerRuleTest {

    @Rule
    val serverRule = MockServerRule(okHttpMockServer())

    @Test
    fun ruleTest() {

    }
}