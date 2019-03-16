# kotlin-mock-server

This is kotlin DSL mock server based OkHttp3 mock server.

🔴 Attention! 
🔨 Work in progress!

Simple
```kotlin
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
```
