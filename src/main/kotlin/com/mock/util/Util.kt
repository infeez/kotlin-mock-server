package com.mock.util

import java.net.DatagramSocket
import java.net.ServerSocket

fun generatePort(): Int {
    val ports = 50013..65535
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
        } catch (t: Throwable) {
            false
        } finally {
            try {
                ss?.close()
            } catch (t: Throwable) {
            }
            try {
                ds?.close()
            } catch (t: Throwable) {
            }
        }
    }
    return port
}
