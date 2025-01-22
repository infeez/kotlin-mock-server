package io.github.infeez.kotlinmockserver.dsl.http.context

import io.github.infeez.kotlinmockserver.mock.Mock
import io.github.infeez.kotlinmockserver.mock.MockConfiguration
import io.github.infeez.kotlinmockserver.mockmodel.MockWebRequest
import io.github.infeez.kotlinmockserver.server.Server
import java.io.Closeable
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The main class that implements the work of the mock server.
 *
 * @param server   - value takes any implementation of [Server] abstract class.
 * @param settings - [MockConfiguration] settings of mock server.
 */
class MockServerContext(
    val server: Server,
    settings: MockConfiguration.() -> Unit
) : Closeable {

    private val lazyMocks = mutableListOf<Lazy<Mock>>()

    val mocks by lazy {
        CopyOnWriteArrayList<Mock>()
    }
    private val requests = mutableMapOf<Int, MockWebRequest>()

    init {
        settings(MockConfiguration)
        server.onDispatch = { webRequest ->
            moveLazyMocks()

            val path = webRequest.path
            val method = webRequest.method
            val body = webRequest.body
            val headers = webRequest.headers

            val foundMock = mocks.find { mock -> mock.isCoincided(path, method, body, headers) }

            if (foundMock?.mockWebResponse != null) {
                requests[foundMock.hashCode()] = webRequest
                foundMock.mockWebResponse
            } else {
                MockConfiguration.defaultResponse
            }
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

    fun add(mock: Lazy<Mock>) {
        lazyMocks.add(mock)
    }

    /**
     * Adding list of mock [Mock] to mocks list.
     *
     * @param mocks [List] of [Mock] to add.
     */
    fun addAll(mocks: List<Mock>) {
        this.mocks.addAll(mocks)
    }

    fun addAllLazy(mocks: List<Lazy<Mock>>) {
        lazyMocks.addAll(mocks)
    }

    /**
     * Adding a [Mock] one by one in mock list.
     *
     * @param mocks vararg of [Mock] to add.
     */
    fun addAll(vararg mocks: Mock) {
        this.mocks.addAll(mocks)
    }

    fun addAll(vararg mocks: Lazy<Mock>) {
        this.lazyMocks.addAll(mocks)
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
        mocks.map { if (it == oldMock) newMock else it }.toMutableList().also {
            mocks.clear()
            mocks.addAll(it)
        }
    }

    /**
     * Returns a list with all successfully mock server request.
     */
    fun getRequests(): List<MockWebRequest> {
        return requests.values.toList()
    }

    /**
     * Returns a [MockWebRequest] successfully mock server request.
     *
     * @param mock - return by this mock hashCode()
     */
    fun getRequestByMock(mock: Mock): MockWebRequest? {
        return requests[mock.hashCode()]
    }

    fun findRequest(path: String): MockWebRequest {
        return requests.values.find {
            request ->
            request.path == path
        } ?: error("Request with path=$path not found")
    }

    fun findRequests(path: String): List<MockWebRequest> {
        return requests.values.filter { request -> request.path == path }
    }

    fun findFirstRequest(path: String): MockWebRequest {
        return requests.values.firstOrNull {
            request ->
            request.path == path
        } ?: error("Request with path=$path not found")
    }

    fun findLastRequest(path: String): MockWebRequest {
        return requests.values.lastOrNull {
            request ->
            request.path == path
        } ?: error("Request with path=$path not found")
    }

    private fun moveLazyMocks() {
        mocks.addAll(
            lazyMocks.map {
                val mock by it
                mock
            }
        )
        lazyMocks.clear()
    }
}
