package com.mock.server

import com.mock.util.generatePort

class Configuration {

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
        fun default(): Configuration {
            return custom {
                port = generatePort()
            }
        }

        /**
         * DSL-context to set configuration params.
         *
         */
        fun custom(block: Configuration.() -> Unit): Configuration {
            return Configuration().apply(block)
        }
    }
}
