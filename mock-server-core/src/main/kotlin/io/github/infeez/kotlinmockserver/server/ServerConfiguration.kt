package io.github.infeez.kotlinmockserver.server

import io.github.infeez.kotlinmockserver.util.Util.generatePort

/**
 * Start-up configuration for mock server.
 *
 */
class ServerConfiguration {

    /**
     * Port used by MockServer.
     *
     * Please be sure the port is not bind!
     */
    var port: Int = -1

    /**
     * Host used by MockServer.
     *
     * localhost by default.
     */
    var host: String = "localhost"

    companion object {

        /**
         * Default configuration.
         *
         * Host always localhost.
         * Port any not bind of 50013 to 65535.
         */
        fun default(): ServerConfiguration {
            return custom {
                port = generatePort()
            }
        }

        /**
         * DSL-context to set configuration params.
         *
         */
        fun custom(block: ServerConfiguration.() -> Unit): ServerConfiguration {
            return ServerConfiguration().apply(block)
        }
    }
}
