package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.MockServerConfiguration
import io.github.infeez.kotlinmockserver.extensions.copyResponse
import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.server.Server
import java.io.Closeable
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

/**
 * The main class that implements the work of the mock server.
 *
 * @param server   - value takes any implementation of [Server] abstract class.
 * @param settings - [MockServerConfiguration] settings of mock server.
 */
class MockServerContext(
    val server: Server,
    settings: MockServerConfiguration.() -> Unit
) : Closeable {

    val mocks = mutableListOf<Mock>()

    init {
        settings(MockServerConfiguration)
        server.onDispatch = { webRequest ->
            val path = webRequest.path
            val method = webRequest.method
            val body = webRequest.body
            val headers = webRequest.headers

            mocks.find { mock -> mock.isCoincided(path, method, body, headers) }?.mockWebResponse ?: MockServerConfiguration.defaultResponse
        }
    }

    override fun close() {
        server.stop()
    }

    /**
     * Method for adding a mock [Mock] using DSL-context.
     *
     */
    fun add(block: MockContext.() -> Unit) {
        addAll(MockContext().apply(block).mocks)
    }

    /**
     * Adding mock to mocks list.
     *
     * @param mock [Mock] to add.
     */
    fun add(mock: Mock) {
        mocks.add(mock)
    }

    /**
     * Adding list of mock [Mock] to mocks list.
     *
     * @param mocks [List] of [Mock] to add.
     */
    fun addAll(mocks: List<Mock>) {
        this.mocks.addAll(mocks)
    }

    /**
     * Adding a [Mock] one by one in mock list.
     *
     * @param mocks vararg of [Mock] to add.
     */
    fun addAll(vararg mocks: Mock) {
        this.mocks.addAll(mocks)
    }

    /**
     * Remove mock by reference from mock list.
     *
     * @param mock - [Mock] to remove.
     */
    fun remove(mock: Mock) {
        mocks.remove(mock)
    }

    /**
     * Replace mock by mock in mocks list.
     *
     * @param oldMock - [Mock] will be replaced
     * @param newMock - [Mock] replaced by
     */
    fun replace(oldMock: Mock, newMock: Mock) {
        if (oldMock == newMock) {
            throw IllegalArgumentException("oldMock equal to newMock!")
        }
        mocks.map { if (it == oldMock) newMock else oldMock }.toMutableList().also {
            mocks.clear()
            mocks.addAll(it)
        }
    }

    inline fun <reified T> changeMockBody(from: Mock, change: T.() -> Unit) {
        val temp = mocks.find { it == from }?.mockWebResponse ?: error("Mock not found!")
        mocks.find { it == from }?.mockWebResponse = temp.copyResponse(change)
    }

    inline fun <T> changeMockBody(type: Type, from: Mock, change: T.() -> Unit) {
        val temp = mocks.find { it == from }?.mockWebResponse ?: error("Mock not found!")
        mocks.find { it == from }?.mockWebResponse = temp.copyResponse(type, change)
    }
}