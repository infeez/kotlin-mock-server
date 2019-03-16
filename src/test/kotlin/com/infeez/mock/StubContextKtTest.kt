package com.infeez.mock

import io.github.rybalkinsd.kohttp.dsl.httpGet
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test


class StubContextKtTest {

    @Test
    fun stubContext() {
        val mockWebServer = MockWebServer()
        mockWebServer.stubContext {
            doResponseWithUrl("/base/mock/server") {
                fromString("response string") {
                    withStatusCode(200)
                    withHeaders {
                        "key" withValue "value"
                    }
                }
            }
        }

        mockWebServer.start()

        val url = mockWebServer.url("/").toString()
        println(url)
        println(mockWebServer.hostName)
        println(mockWebServer.port)
        val response = httpGet {
            host = mockWebServer.hostName
            port = mockWebServer.port
            path = "/base/mock/server"
        }


        response.use {
            println(it.body()!!.string())
        }


        //assertTrue {
          //  response.body()!!.string() == ""
        //}


        mockWebServer.shutdown()
    }

}
