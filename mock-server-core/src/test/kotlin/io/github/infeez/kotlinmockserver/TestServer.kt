package io.github.infeez.kotlinmockserver

import io.github.infeez.kotlinmockserver.mockmodel.MockWebRequest
import io.github.infeez.kotlinmockserver.mockmodel.MockWebResponse
import io.github.infeez.kotlinmockserver.server.Server
import io.github.infeez.kotlinmockserver.server.ServerConfiguration

class TestServer : Server(ServerConfiguration.custom { }) {

    private var timeStart = 0L

    fun request(request: MockWebRequest): MockWebResponse {
        return onDispatch.invoke(request)
    }

    override fun start() {
        timeStart = System.currentTimeMillis()
        println("Test server started")
    }

    override fun stop() {
        println("Test server stopped: ${System.currentTimeMillis() - timeStart} ms")
    }

    override fun getUrl(): String {
        error("Not implemented!")
    }
}
