# kotlin-mock-server [![Build Status](https://travis-ci.com/infeez/kotlin-mock-server.svg?branch=master)](https://travis-ci.com/infeez/kotlin-mock-server) [![](https://jitpack.io/v/infeez/kotlin-mock-server.svg)](https://jitpack.io/#infeez/kotlin-mock-server)

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

    val userInfo = MockEnqueueResponse {
        doResponseWithUrl("userInfo/") {
            fromString(""" { "login" : "user", "username" : "user user" } """) {
                responseStatusCode = 200
            }
        }
    }
    
    val userLoginScenario = listOf(login, userInfo)
}
```
and use with
```kotlin
mockWebServer.mockScenario {
    add(Mocks.login)
    add(Mocks.userInfo)
}
```
```kotlin
mockWebServer.mockScenario {
    addAll(Mocks.userLoginScenario)
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
You can add several responses in add block
```kotlin
mockWebServer.mockScenario {
    add {
        doResponseWithUrl("/base/mock/server1")
        doResponseWithUrl("/base/mock/server2")
        doResponseWithUrl("/base/mock/server3")
    }
}
```
You can set request match params for url and query.
Params for url and query: eq, startWith, endsWith, matches
```kotlin
doResponseWithMatcher(rulePath eq "/some/path") {
    //...                    
}

doResponseWithMatcher(ruleParam("queryParam") eq "1") {
    //...                    
}
```
Matcher params can be combined with: and, or.
```kotlin
doResponseWithMatcher((rulePath eq "/some/path") or (ruleParam("queryParam") eq "1")) {
    //...                    
}
```

Step 1: Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2: Add the dependency
```gradle
dependencies {
    implementation 'com.github.infeez:kotlin-mock-server:X.X.X'
}
```
