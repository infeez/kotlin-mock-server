package com.infeez.mock.util

import java.net.ServerSocket

fun generatePort(): Int {
    val ports = 50013..65535
    var port = 0
    var generated = false
    var index = 0
    while (!generated) {
        var socket = ServerSocket()
        generated = try {
            socket = ServerSocket()
            socket.reuseAddress = true
            port = socket.localPort
            true
        } catch (t: Throwable) {
            false
        } finally {
            try {
                socket.close()
            } catch (t: Throwable) {
            }
        }
    }
    return port
}
