package io.github.infeez.kotlinmockserver.util

import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import java.util.logging.Level
import java.util.logging.Logger

object Util {

    private val logger: Logger = Logger.getLogger(javaClass.name)

    private const val SERVER_PORT_AT = 50013
    private const val SERVER_PORT_TO = 65535
    private val SERVER_PORTS = SERVER_PORT_AT..SERVER_PORT_TO

    internal fun generatePort(): Int {
        val ports = SERVER_PORTS
        var port = 0
        var generated = false
        var index = 0
        while (!generated) {
            port = ports.elementAt(++index)
            var ss: ServerSocket? = null
            var ds: DatagramSocket? = null
            generated = try {
                ss = ServerSocket(port)
                ss.reuseAddress = true
                ds = DatagramSocket(port)
                ds.reuseAddress = true
                true
            } catch (e: IOException) {
                logger.log(Level.WARNING, e.message)
                false
            } finally {
                try {
                    ss?.close()
                } catch (e: IOException) {
                    logger.log(Level.WARNING, e.message)
                }
                try {
                    ds?.close()
                } catch (e: IOException) {
                    logger.log(Level.WARNING, e.message)
                }
            }
        }
        return port
    }
}
