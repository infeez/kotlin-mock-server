package com.mock.server

import com.mock.mockmodel.MockWebRequest
import com.mock.mockmodel.MockWebResponse
import org.junit.rules.ExternalResource
import java.io.Closeable
import java.util.logging.Logger

/**
 * Base abstract class to implement mock server.
 *
 * @param configuration - [Configuration] the configuration of the server.
 */
abstract class Server(
    val configuration: Configuration
) : ExternalResource(), Closeable {

    // TODO change logger to slf4j + logback + kotlin-logging
    protected val logger: Logger = Logger.getLogger(javaClass.name)

    /**
     * This listener needs to call when mock server find mock and ready to give it to client.
     *
     * Please call onDispatch.invoke(mockWebRequest) in your custom server.
     */
    var onDispatch: ((requestMock: MockWebRequest) -> MockWebResponse) = {
        error("Server dispatch listener may not be initialized! You need to initialize onDispatch in your server.\n$it")
    }

    /**
     * Start server called when test before by Rule.
     *
     */
    abstract fun start()

    /**
     * Start server called when test after by Rule.
     *
     */
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