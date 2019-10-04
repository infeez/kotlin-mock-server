# kotlin-mock-server [![Build Status](https://travis-ci.com/infeez/kotlin-mock-server.svg?branch=master)](https://travis-ci.com/infeez/kotlin-mock-server)

This is kotlin DSL mock server based OkHttp3 mock server.

🔴 Attention! 
🔨 Work in progress!

Mocks object sample
```kotlin
object Mocks {

    val login = MockEnqueueResponse {
        doResponseWithUrl("login/") {
            fromString(""" { "login" : "user", "password" : "1234" } """) {
                responseStatusCode = 200
            }
        }
    }
}
```
and use with
```kotlin
mockWebServer.mockScenario {
    add(Mocks.login)
}
```

```kotlin
mockWebServer.mockScenario {
    add {
        doResponseWithUrl("/base/mock/server") {
            fromString("response string") {
                responseStatusCode = 200
                socketPolicy = SocketPolicy.CONTINUE_ALWAYS
                headers {
                    "key" withValue "value"
                }
                bodyDelay {
                    delay = 100
                    unit = TimeUnit.MILLISECONDS
                }
                headersDelay {
                    delay = 100
                    unit = TimeUnit.MILLISECONDS
                }
            }
        }
    }
}
```
