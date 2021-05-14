package com.mock.dsl.http.context

import com.mock.MockServerSettings
import com.mock.extensions.copyResponse
import com.mock.mock.Mock
import com.mock.mockmodel.MockWebResponse
import com.mock.server.Server
import org.junit.rules.ExternalResource
import java.io.Closeable
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class MockServerContext(
    val server: Server,
    settings: MockServerSettings.() -> Unit
) : ExternalResource(), Closeable {

    val mocks = mutableListOf<Mock>()

    init {
        settings(MockServerSettings)
        server.onDispatch = { webRequest ->
            val path = webRequest.path
            val method = webRequest.method
            val body = webRequest.body

            mocks.find { mock -> mock.isCoincided(path, method, body) }?.mockWebResponse ?: getDefaultResponse()
        }
    }

    override fun before() {
        server.start()
    }

    override fun after() {
        server.stop()
    }

    override fun close() {
        server.stop()
    }

    fun add(block: MockContext.() -> Unit) {
        addAll(MockContext().apply(block).mocks)
    }

    fun add(mock: Mock) {
        mocks.add(mock)
    }

    fun addAll(mocks: List<Mock>) {
        this.mocks.addAll(mocks)
    }

    fun addAll(vararg mocks: Mock) {
        this.mocks.addAll(mocks)
    }

    fun remove(mock: Mock) {
        mocks.remove(mock)
    }

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

    private fun getDefaultResponse(): MockWebResponse {
        return MockServerSettings.defaultResponse ?: MockWebResponse(404)
    }
}
