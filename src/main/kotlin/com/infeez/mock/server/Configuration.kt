package com.infeez.mock.server

import com.infeez.mock.util.generatePort

class Configuration {

    var port: Int = -1

    var host: String = "localhost"

    companion object {

        fun default(): Configuration {
            return custom {
                port = generatePort()
            }
        }

        fun custom(block: Configuration.() -> Unit): Configuration {
            return Configuration().apply(block)
        }
    }
}
