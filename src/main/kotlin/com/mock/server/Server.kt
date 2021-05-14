package com.mock.server

import com.mock.mockmodel.MockWebRequest
import com.mock.mockmodel.MockWebResponse
import org.junit.rules.ExternalResource
import java.io.Closeable
import java.util.logging.Logger

abstract class Server(
    val configuration: Configuration
) : ExternalResource(), Closeable {

    // TODO change logger to slf4j + logback + kotlin-logging
    // TEST
    protected val logger: Logger = Logger.getLogger(javaClass.name)

    var onDispatch: ((requestMock: MockWebRequest) -> MockWebResponse) = {
        error("Server dispatch listener may not be initialized! You need to initialize onDispatch in your server.\n$it")
    }

    abstract fun start()

    abstract fun stop()

    override fun before() {
        start()
    }

    override fun after() {
        stop()
    }

    override fun close() {
        stop()
    }
}
