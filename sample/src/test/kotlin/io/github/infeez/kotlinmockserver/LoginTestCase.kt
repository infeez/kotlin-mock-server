package io.github.infeez.kotlinmockserver

import io.github.infeez.kotlinmockserver.LoginMocks.loginByUserJohn
import io.github.infeez.kotlinmockserver.dsl.http.context.MockContext
import io.github.infeez.kotlinmockserver.dsl.http.okhttp.okHttpMockServer
import io.github.infeez.kotlinmockserver.extensions.change
import io.github.infeez.kotlinmockserver.extensions.mocks
import io.github.infeez.kotlinmockserver.junit4.extensions.asRule
import kotlin.test.assertEquals
import kotlin.test.fail
import org.junit.Rule
import org.junit.Test

class LoginTestCase {

    private val mockServer = okHttpMockServer()

    @get:Rule
    val mockServerRule = mockServer.asRule()

    @Test
    fun `login test`() = testWorkFlow({
        +loginByUserJohn
    }) {
        startAppAndLogin("John", "123") {
            checkResultNotError<User> {
                assertEquals("John", username)
                assertEquals("123", password)
                assertEquals("John Doe", fullName)
            }
        }
    }

    @Test
    fun `login incorrect password test`() = testWorkFlow({
        +loginByUserJohn
    }) {
        startAppAndLogin("John", "password incorrect") {
            checkResultErrorMessage {
                assertEquals("Incorrect password", this)
            }
        }
    }

    @Test
    fun `login user not found test`() = testWorkFlow({
        +loginByUserJohn
    }) {
        startAppAndLogin("John123", "123") {
            checkResultErrorMessage {
                assertEquals("User not found", this)
            }
        }
    }

    @Test
    fun `login server error test`() = testWorkFlow({
        +loginByUserJohn
    }) {
        mockServer.change(loginByUserJohn) {
            code(500)
        }
        startAppAndLogin("John123", "123") {
            checkResultErrorMessage {
                assertEquals("Login error. Code: 500", this)
            }
        }
    }

    private inline fun <reified T> Result.checkResultNotError(result: T.() -> Unit) {
        when (this) {
            is Result.Success<*> -> {
                if (data is T) {
                    result(data as T)
                } else {
                    fail("Incorrect type")
                }
            }
            is Result.Error -> {
                fail("Result is error: " + exception.message)
            }
        }
    }

    private fun Result.checkResultErrorMessage(result: String?.() -> Unit) {
        when (this) {
            is Result.Success<*> -> {
                fail("Result is not error!")
            }
            is Result.Error -> {
                result(exception.message)
            }
        }
    }

    private fun startTestApplication(): TestApplication {
        return TestApplication().apply {
            host = mockServer.server.serverConfiguration.host
            port = mockServer.server.serverConfiguration.port
        }
    }

    private fun startAppAndLogin(username: String, password: String, testCase: Result.() -> Unit) {
        startTestApplication().login(username, password).also { testCase(it) }
    }

    private fun testWorkFlow(addMocks: MockContext.() -> Unit, test: () -> Unit) {
        mockServer.mocks(addMocks)
        test()
    }
}
